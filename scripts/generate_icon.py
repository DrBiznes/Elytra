#!/usr/bin/env python3
"""Generate the mod icon without image-generation tools.

Composites the existing Elytra Warp Core item sprite, scaled up with nearest-
neighbor sampling, over a minimal two-color vertical gradient background.
Uses only Python's standard library, matching scripts/generate_texture.py.
"""

from __future__ import annotations

import struct
import zlib
from pathlib import Path

ICON_SIZE = 256
SPRITE_SCALE = 12  # 16x16 sprite -> 192x192, centered with a margin

# Deep plum to dimensional violet, echoing the sprite's own outline and core colors.
GRADIENT_TOP = (43, 37, 61)
GRADIENT_BOTTOM = (94, 61, 133)


def decode_png(path: Path) -> tuple[int, int, list[list[tuple[int, int, int, int]]]]:
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError(f"{path} is not a PNG")
    pos = 8
    width = height = None
    idat = b""
    while pos < len(data):
        length = struct.unpack(">I", data[pos:pos + 4])[0]
        kind = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + length]
        if kind == b"IHDR":
            width, height, bit_depth, color_type = struct.unpack(">IIBB", chunk[:10])
            if bit_depth != 8 or color_type != 6:
                raise ValueError(f"{path} must be 8-bit RGBA")
        elif kind == b"IDAT":
            idat += chunk
        pos += 8 + length + 4

    raw = zlib.decompress(idat)
    stride = width * 4
    rows: list[bytes] = []
    prev = bytes(stride)
    offset = 0
    for _ in range(height):
        filter_type = raw[offset]
        line = bytearray(raw[offset + 1:offset + 1 + stride])
        offset += 1 + stride
        if filter_type == 1:  # Sub
            for i in range(4, stride):
                line[i] = (line[i] + line[i - 4]) & 0xFF
        elif filter_type == 2:  # Up
            for i in range(stride):
                line[i] = (line[i] + prev[i]) & 0xFF
        elif filter_type == 3:  # Average
            for i in range(stride):
                left = line[i - 4] if i >= 4 else 0
                line[i] = (line[i] + (left + prev[i]) // 2) & 0xFF
        elif filter_type == 4:  # Paeth
            for i in range(stride):
                left = line[i - 4] if i >= 4 else 0
                up = prev[i]
                up_left = prev[i - 4] if i >= 4 else 0
                p = left + up - up_left
                pa, pb, pc = abs(p - left), abs(p - up), abs(p - up_left)
                pred = left if pa <= pb and pa <= pc else (up if pb <= pc else up_left)
                line[i] = (line[i] + pred) & 0xFF
        rows.append(bytes(line))
        prev = line

    pixels = [
        [tuple(row[i:i + 4]) for i in range(0, stride, 4)]
        for row in rows
    ]
    return width, height, pixels


def scale_nearest(
    pixels: list[list[tuple[int, int, int, int]]], factor: int
) -> list[list[tuple[int, int, int, int]]]:
    return [
        [pixel for pixel in row for _ in range(factor)]
        for row in pixels
        for _ in range(factor)
    ]


def encode_png(path: Path, pixels: list[list[tuple[int, int, int, int]]]) -> None:
    height = len(pixels)
    width = len(pixels[0])
    raw = b"".join(b"\x00" + bytes(channel for pixel in row for channel in pixel) for row in pixels)

    def chunk(kind: bytes, data: bytes) -> bytes:
        body = kind + data
        return struct.pack(">I", len(data)) + body + struct.pack(">I", zlib.crc32(body) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, level=9))
    png += chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(png)


def lerp(a: int, b: int, t: float) -> int:
    return round(a + (b - a) * t)


def build_background(size: int) -> list[list[tuple[int, int, int, int]]]:
    rows = []
    for y in range(size):
        t = y / (size - 1)
        r = lerp(GRADIENT_TOP[0], GRADIENT_BOTTOM[0], t)
        g = lerp(GRADIENT_TOP[1], GRADIENT_BOTTOM[1], t)
        b = lerp(GRADIENT_TOP[2], GRADIENT_BOTTOM[2], t)
        rows.append([(r, g, b, 255)] * size)
    return rows


def composite(
    background: list[list[tuple[int, int, int, int]]],
    sprite: list[list[tuple[int, int, int, int]]],
) -> list[list[tuple[int, int, int, int]]]:
    size = len(background)
    sprite_size = len(sprite)
    offset = (size - sprite_size) // 2
    canvas = [row[:] for row in background]
    for y, row in enumerate(sprite):
        for x, (r, g, b, a) in enumerate(row):
            if a == 0:
                continue
            bx, by = offset + x, offset + y
            if a == 255:
                canvas[by][bx] = (r, g, b, 255)
            else:
                br, bg, bb, _ = canvas[by][bx]
                t = a / 255
                canvas[by][bx] = (lerp(br, r, t), lerp(bg, g, t), lerp(bb, b, t), 255)
    return canvas


def main() -> None:
    root = Path(__file__).resolve().parents[1]
    sprite_path = root / "src/main/resources/assets/waystone_wings/textures/item/elytra_warp_core.png"
    icon_path = root / "src/main/resources/icon.png"

    _, _, sprite = decode_png(sprite_path)
    scaled_sprite = scale_nearest(sprite, SPRITE_SCALE)

    background = build_background(ICON_SIZE)
    icon = composite(background, scaled_sprite)

    encode_png(icon_path, icon)
    print(f"Generated {icon_path.relative_to(root)} ({ICON_SIZE}x{ICON_SIZE})")


if __name__ == "__main__":
    main()

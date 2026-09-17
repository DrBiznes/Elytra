#!/usr/bin/env python3
"""Generate the 16x16 Elytra Warp Core texture without image-generation tools.

The sprite is deliberately described as a tiny indexed-color pixel map.  This
keeps every pixel intentional, makes palette changes reviewable, and prevents
resampling or antialiasing from creeping into the shipped Minecraft texture.
"""

from __future__ import annotations

import struct
import zlib
from pathlib import Path


TRANSPARENT = (0, 0, 0, 0)

# A compact, saturated palette inspired by Whimscape's crisp, default-adjacent
# approach: colored outlines, warm metal, cool magic, and very sparse highlights.
PALETTE: dict[str, tuple[int, int, int, int]] = {
    ".": TRANSPARENT,
    "d": (43, 37, 61, 255),       # deep plum outline
    "s": (74, 53, 91, 255),       # purple shadow
    "p": (124, 82, 151, 255),     # dimensional violet
    "b": (177, 108, 62, 255),     # warm brass
    "g": (237, 181, 82, 255),     # brass highlight
    "n": (34, 83, 111, 255),      # teal shadow
    "c": (55, 157, 164, 255),     # warp cyan
    "l": (135, 215, 192, 255),    # cyan highlight
    "w": (247, 229, 164, 255),    # tiny warm glint
}

# 16x16, read literally. The outer lobes recall folded Elytra wings while the
# round diamond in the middle reads as a compact teleportation core.
SPRITE = (
    "................",
    ".......gg.......",
    "......dbbd......",
    ".....dbggbd.....",
    "..dsdddnndddsd..",
    ".dbpddcllcddpbd.",
    "dsgpddlwwlddpgsd",
    "dcpdddlwwldddpcd",
    "dscnddcllcddncsd",
    ".dbpddbccbddpbd.",
    "..dsdddbbdddsd..",
    "...ddddbgdddd...",
    ".....dbggbd.....",
    "......dbbd......",
    ".......dd.......",
    "................",
)


def encode_png(path: Path, pixels: list[list[tuple[int, int, int, int]]]) -> None:
    """Write an RGBA PNG using only Python's standard library."""
    height = len(pixels)
    width = len(pixels[0])
    if any(len(row) != width for row in pixels):
        raise ValueError("Every pixel row must have the same width")

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


def scale_nearest(
    pixels: list[list[tuple[int, int, int, int]]], factor: int
) -> list[list[tuple[int, int, int, int]]]:
    return [
        [pixel for pixel in row for _ in range(factor)]
        for row in pixels
        for _ in range(factor)
    ]


def main() -> None:
    if len(SPRITE) != 16 or any(len(row) != 16 for row in SPRITE):
        raise ValueError("The Minecraft item sprite must be exactly 16x16")

    pixels = [[PALETTE[index] for index in row] for row in SPRITE]
    root = Path(__file__).resolve().parents[1]
    texture = root / "src/main/resources/assets/waystone_wings/textures/item/elytra_warp_core.png"
    preview = root / "docs/art/elytra_warp_core_16x_preview.png"

    encode_png(texture, pixels)
    encode_png(preview, scale_nearest(pixels, 16))
    print(f"Generated {texture.relative_to(root)} (16x16)")
    print(f"Generated {preview.relative_to(root)} (256x256 nearest-neighbor preview)")


if __name__ == "__main__":
    main()

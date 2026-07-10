#!/usr/bin/env python

import argparse
from os import stat


def trim_trailing_zeros_keep_one(input_file):
    """Trim trailing 0x00 bytes, keeping only one if present."""
    file1 = open(input_file, 'rb+')
    file1.seek(0, 2)
    filesize = file1.tell()

    if filesize == 0:
        file1.close()
        return

    trailing_zeros = 0
    pos = filesize - 1
    while pos >= 0:
        file1.seek(pos)
        if file1.read(1) != b'\x00':
            break
        trailing_zeros += 1
        pos -= 1

    if trailing_zeros > 1:
        file1.truncate(filesize - trailing_zeros + 1)

    file1.close()


def padding_file(input_file, align_num):
    """Fill 0 to make input_file's size a multiple of align_num."""
    filesize = stat(input_file).st_size
    file1 = open(input_file, 'ab+')
    padding = filesize % align_num
    if padding != 0:
        padding = align_num - padding
        for _ in range(padding):
            file1.write("\x00".encode())
    file1.close()


def append_file(img_file, cert_file, alignment):
    """Append provided cert_file to the end of target img_file."""
    padding_file(img_file, alignment)
    file1 = open(img_file, 'ab+')
    file2 = open(cert_file, 'rb')
    file1.write(file2.read())
    file1.close()
    file2.close()


def main():
    parser = argparse.ArgumentParser(description='Sign an image with provided der certs')
    parser.add_argument('--alignment', type=int, help='Alignment for calculating padding')
    parser.add_argument('--cert1', type=str, help='Certificate - cert1.der')
    parser.add_argument('--cert2', type=str, help='Certificate - cert2.der')
    parser.add_argument('--dtbo', type=str, help='Path to the DTBO img')
    args = parser.parse_args()

    trim_trailing_zeros_keep_one(args.dtbo)
    append_file(args.dtbo, args.cert1, args.alignment)
    append_file(args.dtbo, args.cert2, args.alignment)


if __name__ == '__main__':
    main()

# coding:utf-8
import cv2
import sys
import numpy as np

if __name__ == "__main__":
    sys.argv
    img = cv2.imread(sys.argv[1], cv2.IMREAD_UNCHANGED)
    print(img.shape)
    if len(img.shape) == 2:
        bgraImg = cv2.merge((img, img, img, img))
        cv2.imwrite(sys.argv[2], bgraImg)

import cv2
import numpy as np
from numpy import linalg
import numpy.linalg as npl
import math
from distance import pdist
from feature_extraction import feature_extraction
from math import ceil

from get_bags_of_words import img_to_hist


def get_sub_images(img, level) -> list:
    w, h, c = img.shape
    n_partition = 2 ** level
    dw, dh = ceil(w / n_partition), ceil(h / n_partition)
    sub_image_list = []
    for i in range(n_partition):
        start_w = i * dw
        for j in range(n_partition):
            start_h = j * dh
            sub_image_list.append(img[start_w: start_w + dw, start_h: start_h + dh])
    return sub_image_list


def get_spatial_pyramid_feats(image_paths, max_level, feature):
    """
    This function assumes that 'vocab_hog.npy' (for HoG) or 'vocab_sift.npy' (for SIFT)
    exists and contains an N x feature vector length matrix 'vocab' where each row
    is a kmeans centroid or visual word. This matrix is saved to disk rather than passed
    in a parameter to avoid recomputing the vocabulary every run.

    :param image_paths: a N array of string where each string is an image path,
    :param max_level: level of pyramid,
    :param feature: name of image feature representation.

    :return: an N x d matrix, where d is the dimensionality of the
        feature representation. In this case, d will equal the number
        of clusters or equivalently the number of entries in each
        image's histogram ('vocab_size'), multiplies with
        (1 / 3) * (4 ^ (max_level + 1) - 1).
        e.g. 1, 5, 21, ...
    """
    if feature == 'HoG':
        vocab = np.load('vocab_hog.npy')
    elif feature == 'SIFT':
        vocab = np.load('vocab_sift.npy')
    else:
        raise ValueError

    # Your code here. You should also change the return value.
    list_of_hist = []
    for idx, path in enumerate(image_paths):
        img = cv2.imread(path)[:, :, ::-1]

        list_of_level_hist = []
        for lv in range(max_level + 1):

            sub_images = get_sub_images(img, lv)

            list_of_sub_hist = []
            for sub_img in sub_images:
                sub_hist = img_to_hist(sub_img, feature, vocab)
                list_of_sub_hist.append(sub_hist)

            level_hist = np.concatenate(list_of_sub_hist)
            level_hist = (2 ** (lv - max_level)) * level_hist
            list_of_level_hist.append(level_hist)

        hist = np.concatenate(list_of_level_hist)
        hist = hist / npl.norm(hist)
        list_of_hist.append(hist)

        if idx % 150 == 0:
            print("get_spatial_pyramid_feats: {}% ({}/{})".format(
                100 * round(idx / len(image_paths), 3), idx, len(image_paths)))

    bow_features = np.vstack(list_of_hist)

    # return np.zeros((1500, 36))
    return bow_features

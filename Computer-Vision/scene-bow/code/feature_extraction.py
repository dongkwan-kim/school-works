import cv2
import numpy as np
import numpy.linalg as npl
import math


def get_grid_list(img, grid_size) -> list:
    w, h, c = img.shape
    w_partition = int(math.ceil(w / grid_size))
    h_partition = int(math.ceil(h / grid_size))
    sub_image_list = []
    for i in range(w_partition):
        start_w = i * grid_size
        for j in range(h_partition):
            start_h = j * grid_size
            sub_image_list.append(img[start_w: start_w + grid_size, start_h: start_h + grid_size])
    return sub_image_list


def get_grid_coord_list(img, grid_size) -> list:
    w, h, c = img.shape
    w_partition = int(math.ceil(w / grid_size))
    h_partition = int(math.ceil(h / grid_size))
    coord_list = []
    for i in range(w_partition):
        start_w = i * grid_size
        for j in range(h_partition):
            start_h = j * grid_size
            coord_list.append((start_w, start_h))
    return coord_list


def feature_extraction(img, feature):
    """
    This function computes defined feature (HoG, SIFT) descriptors of the target image.
    :param img: a height x width x channels matrix,
    :param feature: name of image feature representation.
    :return: a N x feature_size matrix.
    """

    if feature == 'HoG':
        # HoG parameters
        win_size = (32, 32)
        block_size = (32, 32)
        block_stride = (16, 16)
        cell_size = (16, 16)
        nbins = 9
        deriv_aperture = 1
        win_sigma = 4
        histogram_norm_type = 0
        l2_hys_threshold = 2.0000000000000001e-01
        gamma_correction = 0
        nlevels = 64

        # Your code here. You should also change the return value.
        hog = cv2.HOGDescriptor(
            win_size, block_size, block_stride, cell_size, nbins, deriv_aperture,
            win_sigma, histogram_norm_type, l2_hys_threshold, gamma_correction, nlevels)
        grid_coord_list = get_grid_coord_list(img, 16)

        feature_list = []
        for grid_coord in grid_coord_list:
            feature_of_grid = hog.compute(
                img, winStride=(16, 16), locations=(grid_coord,))
            feature_of_grid = feature_of_grid.flatten()
            if np.sum(feature_of_grid) != 0:
                feature_list.append(feature_of_grid)

        if not feature_list:
            feature_ndarray = None
        else:
            feature_ndarray = np.vstack(feature_list)
        # (1500, 36)
        return feature_ndarray

    elif feature == 'SIFT':

        # Your code here. You should also change the return value.
        sift = cv2.xfeatures2d.SIFT_create()
        _, features = sift.detectAndCompute(img, None)

        if features is not None:
            features = features / (npl.norm(features, axis=1, keepdims=True) + 1e-10)
        else:
            features = None

        # (1500, 128)
        return features

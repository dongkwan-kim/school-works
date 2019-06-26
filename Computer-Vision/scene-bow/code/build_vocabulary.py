import cv2
import numpy as np

from feature_extraction import feature_extraction
from kmeans_clustering import kmeans_clustering


def build_vocabulary(image_paths, vocab_size, feature):
    """
    This function will sample feature descriptors from the training images,
    cluster them with kmeans, and the return the cluster centers.

    :param image_paths: a N array of string where each string is an image path
    :param vocab_size: the size of the vocabulary.
    :param feature: name of image feature representation.

    :return: a vocab_size x feature_size matrix. center positions of k-means clustering.
    """
    all_features = []

    for path in image_paths:
        img = cv2.imread(path)[:, :, ::-1]  # 이미지 읽기

        features = feature_extraction(img, feature)  # 이미지에서 feature 추출
        all_features.append(features)  # feature들을 리스트에 추가

    all_features = np.concatenate(all_features, 0)  # 모든 feature들을 붙여서 하나의 matrix 생성

    # k-means clustering
    centers = kmeans_clustering(all_features, vocab_size, 1e-4, 100)

    return centers  # k-means clustering 결과의 center 값들을 반환

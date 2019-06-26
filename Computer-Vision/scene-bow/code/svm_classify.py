import numpy as np
from sklearn import svm
from sklearn.multiclass import OneVsRestClassifier


def svm_classify(train_image_feats, train_labels, test_image_feats, kernel_type):
    """
    This function should train a linear SVM for every category (i.e., one vs all)
    and then use the learned linear classifiers to predict the category of every
    test image. Every test feature will be evaluated with all 15 SVMs and the
    most confident SVM will 'win'.

    :param train_image_feats: an N x d matrix, where d is the dimensionality of the feature representation.
    :param train_labels: an N array, where each entry is a string indicating the ground truth category
        for each training image.
    :param test_image_feats: an M x d matrix, where d is the dimensionality of the feature representation.
        You can assume M = N unless you've modified the starter code.
    :param kernel_type: SVM kernel type. 'linear' or 'RBF'

    :return:
        an M array, where each entry is a string indicating the predicted
        category for each test image.
    """

    categories = np.unique(train_labels)

    # Your code here. You should also change the return value.
    clf = svm.SVC(gamma="scale", random_state=42, kernel=kernel_type.lower())
    one_vs_all_clf = OneVsRestClassifier(clf)
    one_vs_all_clf.fit(train_image_feats, train_labels)

    # return np.array([categories[0]] * 1500)
    return one_vs_all_clf.predict(test_image_feats)

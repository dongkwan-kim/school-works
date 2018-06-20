import tensorflow as tf
import numpy as np
import os

tf.logging.set_verbosity(tf.logging.INFO)

DEPTH = 5


def stack_layer(before_layer, shape, _activation=tf.nn.relu):
    """
    :param before_layer:
    :param shape: [x y]
    :param _activation:
    :return:
    """
    # weight: [x y], bias [y]
    weight_shape = shape
    bias_shape = weight_shape[1:]

    weight = tf.Variable(tf.truncated_normal(weight_shape, stddev=0.01))
    bias = tf.Variable(tf.constant(0.0, shape=bias_shape))
    out = _activation(tf.matmul(before_layer, weight) + bias)

    return weight, bias, out


def custom_model_fn(features, labels, mode):
    """Model function for PA1"""

    learning_rate = 0.001
    hidden_size = 900

    # Write your custom layer
    # Input Layer
    input_layer = tf.reshape(features["x"], [-1, 784])  # You also can use 1 x 784 vector

    w1, b1, h1 = stack_layer(input_layer, [784, hidden_size])
    w2, b2, h2 = stack_layer(h1, [hidden_size, hidden_size])

    # DEPTH >= 5
    w3, b3, h3 = stack_layer(h2, [hidden_size, hidden_size])
    w4, b4, h4 = stack_layer(h3, [hidden_size, hidden_size])

    # DEPTH >= 7
    w5, b5, h5 = stack_layer(h4, [hidden_size, hidden_size])
    w6, b6, h6 = stack_layer(h5, [hidden_size, hidden_size])

    # Output logits Layer
    if DEPTH == 3:
        logits = tf.layers.dense(inputs=h2, units=10)
    elif DEPTH == 5:
        logits = tf.layers.dense(inputs=h4, units=10)
    elif DEPTH == 7:
        logits = tf.layers.dense(inputs=h6, units=10)
    else:
        raise Exception('Unexpected number')

    predictions = {
        # Generate predictions (for PREDICT and EVAL mode)
        "classes": tf.argmax(input=logits, axis=1),
        # Add `softmax_tensor` to the graph. It is used for PREDICT and by the
        # `logging_hook`.
        "probabilities": tf.nn.softmax(logits, name="softmax_tensor")
    }

    # In predictions, return the prediction value, do not modify
    if mode == tf.estimator.ModeKeys.PREDICT:
        return tf.estimator.EstimatorSpec(mode=mode, predictions=predictions)

    # Select your loss and optimizer from tensorflow API
    # Calculate Loss (for both TRAIN and EVAL modes)
    # loss = tf.losses."custom loss function" # Refer to tf.losses
    loss = tf.losses.sparse_softmax_cross_entropy(labels=labels, logits=logits)

    # Configure the Training Op (for TRAIN mode)
    if mode == tf.estimator.ModeKeys.TRAIN:
        # optimizer = tf.train."custom optimizer" # Refer to tf.train
        optimizer = tf.train.AdamOptimizer(learning_rate)
        train_op = optimizer.minimize(loss=loss, global_step=tf.train.get_global_step())
        return tf.estimator.EstimatorSpec(mode=mode, loss=loss, train_op=train_op)

    # Add evaluation metrics (for EVAL mode)
    eval_metric_ops = {"accuracy": tf.metrics.accuracy(labels=labels, predictions=predictions["classes"])}
    return tf.estimator.EstimatorSpec(mode=mode, loss=loss, eval_metric_ops=eval_metric_ops)


if __name__ == '__main__':

    ONLY_EVAL = False
    PATH = 'cs492c_assignment1_data'

    total_steps = 25000
    interval = 1000

    dataset_train = np.load(os.path.join(PATH, 'train.npy'))
    dataset_eval = np.load(os.path.join(PATH, 'valid.npy'))
    test_data = np.load(os.path.join(PATH, 'test.npy'))

    train_data = dataset_train[:, :784]
    train_labels = dataset_train[:, 784].astype(np.int32)
    eval_data = dataset_eval[:, :784]
    eval_labels = dataset_eval[:, 784].astype(np.int32)

    # Save model and checkpoint
    # $ tensorboard --logdir=model_{0}
    classifier = tf.estimator.Estimator(model_fn=custom_model_fn, model_dir="./model_{0}/".format(DEPTH))

    # Set up logging for predictions
    tensors_to_log = {"probabilities": "softmax_tensor"}
    logging_hook = tf.train.LoggingTensorHook(tensors=tensors_to_log, every_n_iter=50)

    (steps_argmax_accuracy, max_accuracy) = (-1, -1)
    for eval_no in range(1, int(total_steps / interval) + 1):

        # Train the model. You can train your model with specific batch size and epoches
        if not ONLY_EVAL:
            train_input = tf.estimator.inputs.numpy_input_fn(x={"x": train_data},
                                                             y=train_labels, batch_size=100, num_epochs=None,
                                                             shuffle=True)
            classifier.train(input_fn=train_input, steps=interval, hooks=[logging_hook])

        # Eval the model. You can evaluate your trained model with validation data
        eval_input = tf.estimator.inputs.numpy_input_fn(x={"x": eval_data},
                                                        y=eval_labels, num_epochs=1, shuffle=False)
        eval_results = classifier.evaluate(input_fn=eval_input)
        (steps_argmax_accuracy, max_accuracy, changed) = (eval_results['global_step'], eval_results['accuracy'], True) \
            if eval_results['accuracy'] > max_accuracy else (steps_argmax_accuracy, max_accuracy, False)
        print(eval_results)

        # Predict the test dataset for steps that maximize the eval_accuracy
        # Do not modify!!!
        if changed:
            pred_input = tf.estimator.inputs.numpy_input_fn(x={"x": test_data}, shuffle=False)
            pred_results = classifier.predict(input_fn=pred_input)
            pred_list = list(pred_results)
            result = np.asarray([list(x.values())[0] for x in pred_list])

            print('Saved {0} steps (acc: {1})'.format(steps_argmax_accuracy, max_accuracy))
            np.save('20183060_network_{0}.npy'.format(DEPTH), result)

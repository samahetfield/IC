from keras.datasets import mnist

mnist.load_data()

# imports for array-handling and plotting
import numpy as np
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt

# let's keep our keras backend tensorflow quiet
import os
os.environ['TF_CPP_MIN_LOG_LEVEL']='3'

# keras imports for the dataset
from keras.datasets import mnist
from keras.models import Sequential, load_model
from keras.layers.core import Dense, Dropout, Activation
from keras.utils import np_utils

(X_train, y_train), (X_test, y_test) = mnist.load_data()

text_file = open("Output.txt", "w")
for x in range(0, 10000):
    text_file.write(str(y_train[x]))

text_file.close()


# Vemos las dimensiones iniciales
print("X_train tamaño, dimensiones", X_train.shape)
print("y_train tamaño, dimensiones", y_train.shape)
print("X_test tamaño, dimensiones", X_test.shape)
print("y_test tamaño, dimensiones", y_test.shape)

# Creamos el ventor de entrada para las imágenes 28x28 en un vector de una dimensión
X_train = X_train.reshape(60000, 784)
X_test = X_test.reshape(10000, 784)
X_train = X_train.astype('float32')
X_test = X_test.astype('float32')

# Normalizamos datos
X_train /= 255
X_test /= 255

print("Dimensión de la matriz de entrenamiento", X_train.shape)
print("Dimensión de la matriz de test", X_test.shape)

# Transformamos a vectores binarios
n_classes = 10
print("Shape before one-hot encoding: ", y_train.shape)
Y_train = np_utils.to_categorical(y_train, n_classes)
Y_test = np_utils.to_categorical(y_test, n_classes)
print("Shape after one-hot encoding: ", Y_train.shape)

print("Etiqueta de un número antes de transformar: ", y_train[0])
print("Etiqueta de un número antes de transformar: ", Y_train[0])


# Construimos el modelo
model = Sequential()
model.add(Dense(784, input_shape=(784,)))
model.add(Activation('tanh'))
model.add(Dropout(0.2))

model.add(Dense(512))
model.add(Activation('relu'))
model.add(Dropout(0.2))


model.add(Dense(10))
model.add(Activation('softmax'))



# Compilamos el modelo
model.compile(loss='categorical_crossentropy', metrics=['accuracy'], optimizer='Adam')

# Entrenamos el modelo
history = model.fit(X_train, Y_train,
          batch_size=128, epochs=20,
          verbose=2,
          validation_data=(X_test, Y_test))

# Imprimimos métricas
fig = plt.figure()
plt.subplot(2,1,1)
plt.plot(history.history['acc'])
plt.plot(history.history['val_acc'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='lower right')

plt.subplot(2,1,2)
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper right')

plt.tight_layout()

plt.show()



# Hacemos el test
mnist_model = model
loss_and_metrics = mnist_model.evaluate(X_test, Y_test, verbose=2)


print("Test Loss", loss_and_metrics[0])
print("Test Accuracy", loss_and_metrics[1])

predicted_classes = mnist_model.predict_classes(X_test)

# Obtenemos las predcciones correctas e incorrectas
correct_indices = np.nonzero(predicted_classes == y_test)[0]
incorrect_indices = np.nonzero(predicted_classes != y_test)[0]
print()
print(len(correct_indices)," clasificados correctamente")
print(len(incorrect_indices)," erróneos")

# adapto imagen para 9 subplots
plt.rcParams['figure.figsize'] = (7,14)

figure_evaluation = plt.figure()

# plot 9 predicciones incorrectas
for i, incorrect in enumerate(incorrect_indices[:9]):
    plt.subplot(6,3,i+1)
    plt.imshow(X_test[incorrect].reshape(28,28), cmap='gray', interpolation='none')
    plt.title(
      "Predicho {}, Valor: {}".format(predicted_classes[incorrect],
                                       y_test[incorrect]))
    plt.xticks([])
    plt.yticks([])

plt.show()
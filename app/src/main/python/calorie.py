import cv2
import numpy as np 
import os 
import argparse
import glob 
import io
import base64
import pandas as pd
from PIL import Image, ImageEnhance
from matplotlib import pyplot as plt, image as mpimg
from os.path import dirname,join
from operator import itemgetter


def funt(data):

        weights= join(dirname(__file__),"yolov3-tiny_obj_best_new.weights")
        cfg= join(dirname(__file__),"yolov3-tiny_obj_new.cfg")
        names= join(dirname(__file__),"obj_new.names")


        net = cv2.dnn.readNet(cfg,weights)
        net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
        net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)
        classes = []

        confThreshold = 0.20
        nmsThreshold = 0.6

        with open(names,'rt') as f:
           classes =[line.rstrip() for line in f]

        COLORS = np.random.randint(0, 255, size=(len(classes), 3),dtype="uint8")

        layer_names = net.getLayerNames()
        output_layers = [layer_names[i[0]-1] for i in net.getUnconnectedOutLayers()]

        decoded_data=base64.b64decode(data)
        np_data=np.fromstring(decoded_data,np.uint8)
        img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)

        height, width = img.shape[:2]
        blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), swapRB=True, crop=False)
        net.setInput(blob)
        layer_outputs = net.forward(output_layers)
        class_ids, confidences, b_boxes = [], [], []
        for output in layer_outputs:
            for detection in output:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]

                if confidence > confThreshold:
                    center_x, center_y, w, h = (detection[0:4] * np.array([width, height, width, height])).astype('int')

                    x = int(center_x - w / 2)
                    y = int(center_y - h / 2)

                    b_boxes.append([x, y, int(w), int(h)])
                    confidences.append(float(confidence))
                    class_ids.append(int(class_id))


        indices = cv2.dnn.NMSBoxes(b_boxes, confidences, confThreshold, nmsThreshold).flatten().tolist()


        with open(names, "r") as f:
            classes = [line.strip() for line in f.readlines()]
        colors = np.random.uniform(0, 255, size=(len(classes), 3))


        for index in indices:
            x, y, w, h = b_boxes[index]
            cv2.rectangle(img, (x, y), (x + w, y + h), colors[1], 2)
            cv2.putText(img, classes[class_ids[index]], (x - 7 , y+10 ), cv2.FONT_HERSHEY_COMPLEX_SMALL, 0.75, colors[index], 1)


        return classes[class_ids[index]]


def readCal(name):
    calorieXL= join(dirname(__file__),"caloriexl.csv")
    df=pd.read_csv(calorieXL)
    ind=df[df['Item'] == name].index  # .values on older versions
    k=df['Calorie'][ind]
    k=k.to_numpy()
    return k


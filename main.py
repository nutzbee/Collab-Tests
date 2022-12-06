from flask import Flask, request, render_template
import pickle

import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.cm as cm
import numpy as np
import seaborn as sns
from sklearn import metrics, datasets, cluster
from pandas import DataFrame
from sklearn.model_selection import train_test_split
from sklearn import model_selection
from sklearn.neighbors import KNeighborsClassifier
from sklearn.datasets import make_blobs
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_samples, silhouette_score
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import StandardScaler
from itertools import cycle, islice
from pandas.plotting import parallel_coordinates
from sklearn.preprocessing import LabelEncoder

dataframe = pd.read_csv('Food_Preference.csv')
example_df = dataframe[['Gender','Age','Food','Juice','Dessert']]

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/predict',methods=['POST'])
def predict():
    '''User's input from the form'''
    gender = request.form["gender"]
    age = int(request.form["age"])
    food = request.form["food"]
    juice = request.form["juice"]
    dessert = request.form["dessert"]
    '''Compiling'''
    users_inputex = [gender,age,food,juice,dessert]
    example_df1 = example_df.append(pd.Series(users_inputex, index=example_df.columns[:len(users_inputex)]), ignore_index=True)
    '''Preprocessing'''
    le = LabelEncoder()
    example_df1['Gender'] = le.fit_transform(example_df1['Gender'])
    example_df1['Food'] = le.fit_transform(example_df1['Food'])
    example_df1['Juice'] = le.fit_transform(example_df1['Juice'])
    example_df1['Dessert'] = le.fit_transform(example_df1['Dessert'])
    example_df1 = example_df1.fillna(0)
    kmeans = KMeans(n_clusters=3)
    ex = example_df1
    y_predict = kmeans.fit_predict(ex)
    example_df1['cluster'] = y_predict
    '''Getting user's output'''
    cluster = example_df1.iloc[-1]['cluster']
    
    return render_template('index.html', cluster_text=f'You belong to cluster {cluster}')

if __name__ == "__main__":
    app.run()
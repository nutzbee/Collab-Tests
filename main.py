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
file = open('clustered_food.pkl', 'rb')
data = pickle.load(file)
diadata = pd.read_csv('diabetes.csv')

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

    pregnancy = request.form["pregnancy"]
    glucose = request.form["glucose"]
    bp = request.form["bloodpressure"]
    skinthick = request.form["skinthickness"]
    insulin = request.form["insulin"]
    bmi = request.form["bmi"]
    dpf = request.form["dpf"]

    '''Compiling'''
    users_inputex = [gender,age,food,juice,dessert]
    users_diainput = [pregnancy, glucose, bp, skinthick, insulin, bmi, dpf, age]
    example_df1 = example_df.append(pd.Series(users_inputex, index=example_df.columns[:len(users_inputex)]), ignore_index=True)
    diadf = diadata.append(pd.Series(users_diainput, index=diadata.columns[:len(users_diainput)]), ignore_index=True)
    
    '''Preprocessing'''
    le = LabelEncoder()
    example_df1['Gender'] = le.fit_transform(example_df1['Gender'])
    example_df1['Food'] = le.fit_transform(example_df1['Food'])
    example_df1['Juice'] = le.fit_transform(example_df1['Juice'])
    example_df1['Dessert'] = le.fit_transform(example_df1['Dessert'])

    example_df1 = example_df1.fillna(0)
    diadf = diadf.fillna(0)

    '''Algorithm'''
    kmeans = KMeans(n_clusters=3)
    ex = example_df1
    y_predict = kmeans.fit_predict(ex)
    y_diapredict = kmeans.fit_predict(diadf)
    example_df1['cluster'] = y_predict
    diadf['cluster'] = y_diapredict


    '''Getting user's output'''
    cluster = example_df1.iloc[-1]['cluster']
    diacluster = diadf.iloc[-1]['cluster']
    if dessert != 'no':
        data2 = data[data['cluster'] == cluster]
        my_dessert = data2[data2['NDB_No'] >= 43000]
        my_dessert = my_dessert[my_dessert['NDB_No'] < 43599]
        my_dessert = my_dessert.Shrt_Desc.to_string(index=False)
    else :
        my_dessert = "You don't want any dessert"
    
    return render_template('index.html', cluster_text=f'You belong to cluster {cluster+1} and {diacluster+1}',
    reco_text=f'{my_dessert}')

if __name__ == "__main__":
    app.run()
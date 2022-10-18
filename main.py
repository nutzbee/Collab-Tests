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

dataframe = pickle.load(open('example.pkl', 'rb'))
example_df = dataframe[['NDB_No','Water_(g)','Energ_Kcal',
'Protein_(g)','Lipid_Tot_(g)','Ash_(g)','Carbohydrt_(g)']]
max = int(dataframe.iloc[-1]['NDB_No'])

app = Flask(__name__)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/predict',methods=['POST'])
def predict():
    '''User's input from the form'''
    foodId = max+1
    food_name = request.form["foodName"]
    data1 = int(request.form["nutrient1"])
    data2 = int(request.form["nutrient2"])
    data3 = int(request.form["nutrient3"])
    data4 = int(request.form["nutrient4"])
    data5 = int(request.form["nutrient5"])
    data6 = int(request.form["nutrient6"])
    data7 = request.form["food"]
    '''Compiling'''
    users_inputdf1 = [foodId,food_name,data1,data2,data3,data4,data5,data6]
    users_inputex = [foodId,data1,data2,data3,data4,data5,data6]
    example_df1 = example_df.append(pd.Series(
        users_inputex, index = example_df.columns[:len(users_inputex)]), ignore_index=True)
    dataframe1 = dataframe.append(pd.Series(
        users_inputdf1, index=dataframe.columns[:len(users_inputdf1)]), ignore_index=True)
    '''Preprocessing'''
    example_df1 = example_df1.fillna(0)
    ex = StandardScaler().fit_transform(example_df1)
    kmeans = KMeans(n_clusters=4)
    kmeans.fit(ex)
    y_predict = kmeans.fit_predict(example_df1)
    example_df1['cluster'] = y_predict
    example_df1 = example_df1[['NDB_No','cluster']]
    example_df1 = pd.merge(dataframe1, example_df1)
    '''Getting user's output'''
    source = example_df1[example_df1['NDB_No'] == foodId]
    final_food_name = source.iloc[-1]['Shrt_Desc']
    cluster = source.iloc[-1]['cluster']
    final_foodId = source.iloc[-1]['NDB_No']
    rer = example_df1[example_df1['cluster'] == cluster]
    reco = rer[rer['Shrt_Desc'].str.contains(data7, case=False)]
    
    return render_template('index.html',
    cluster_text=f'Your food belongs to cluster {cluster} with a name of {final_food_name} and food database number of {final_foodId}',
    food_name=f'{final_food_name}', reco_text=f'{reco}')

'''@app.route('/reco',methods=['POST'])
def reco():
    data7 = request.form["food"]
    reco = predict.example_df1[predict.example_df1['Shrt_Desc'].str.contains(data7, case=False)]
    return render_template('idex.html', reco_text=f'{reco}')'''
if __name__ == "__main__":
    app.run()
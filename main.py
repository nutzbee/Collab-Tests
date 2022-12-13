from flask import Flask, request, render_template, jsonify
import pickle

import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder

dataframe = pd.read_csv('mergedDf.csv')
example_df = dataframe[[
    'Gender','Age','Food','Juice','Dessert','Pregnancies','Glucose','BloodPressure',
    'SkinThickness', 'Insulin', 'BMI', 'DiabetesPedigreeFunction'
]]
file = open('clustered_food.pkl', 'rb')
data = pickle.load(file)

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
    users_inputex = [gender,age,food,juice,dessert, pregnancy, glucose, bp, skinthick, insulin, bmi, dpf]
    example_df1 = example_df.append(pd.Series(users_inputex, index=example_df.columns[:len(users_inputex)]), ignore_index=True)
    
    '''Preprocessing'''
    le = LabelEncoder()

    example_df1['Gender'] = le.fit_transform(example_df1['Gender'])
    example_df1['Food'] = le.fit_transform(example_df1['Food'])
    example_df1['Juice'] = le.fit_transform(example_df1['Juice'])
    example_df1['Dessert'] = le.fit_transform(example_df1['Dessert'])

    '''Algorithm'''
    kmeans = KMeans(n_clusters=3)
    ex = example_df1
    y_predict = kmeans.fit_predict(ex)
    example_df1['cluster'] = y_predict

    '''Getting user's output'''
    cluster = example_df1.iloc[-1]['cluster']
    if dessert != 'no':
        data2 = data[data['cluster'] == cluster]
        my_dessert = data2[data2['NDB_No'] >= 43000]
        my_dessert = my_dessert[my_dessert['NDB_No'] < 43599]
        my_dessert = my_dessert.Shrt_Desc.to_list()
    else :
        my_dessert = "You don't want any dessert"
    
    return render_template('predict.html',
    cluster_text=f'You belong to cluster {cluster+1}',
    reco_text=f'{my_dessert}')

if __name__ == "__main__":
    app.run(debug=True)
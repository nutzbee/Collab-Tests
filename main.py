from flask import Flask, request, render_template, jsonify
import pickle,pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from IPython.display import HTML

dataframe = pd.read_csv('mergedDf.csv')
example_df = dataframe[[
    'Gender','Age','Food','Juice','Dessert','Pregnancies','Glucose','BloodPressure',
    'SkinThickness', 'Insulin', 'BMI'
]]
file = open('clustered_food.pkl', 'rb')
data = pickle.load(file)

app = Flask(__name__)

@app.route('/')
def home():
    drinks = data[data['NDB_No'] >= 14000]
    drinks = drinks[drinks['NDB_No'] < 14655]
    drinks = drinks.sample(n=5)
    drinks = HTML(drinks[['Shrt_Desc', 'Energ_Kcal']].to_html(classes='table table-stripped', index=False))
    
    desserts = data[data['NDB_No'] >= 43000]
    desserts = desserts[desserts['NDB_No'] < 43599]
    desserts = desserts.sample(n=5)
    desserts = HTML(desserts[['Shrt_Desc', 'Energ_Kcal']].to_html(classes='table table-stripped', index=False))
    
    return render_template('index.html',
    reco_text_dessert=desserts,
    reco_text_drinks=drinks,
    button_text="Submit")

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

    '''Compiling'''
    if gender == 'male':
        pregnancy = 0
    users_inputex = [gender,age,food,juice,dessert, pregnancy, glucose, bp, skinthick, insulin, bmi]
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
    data2 = data[data['cluster'] == cluster]

    my_drinks = data2[data2['NDB_No'] >= 14000]
    my_drinks = my_drinks[my_drinks['NDB_No'] < 14655]
    my_drinks = my_drinks.rename(columns = {'Shrt_Desc':'Food Name of your drinks', 'Energ_Kcal':'Kilocalorie amount'})
    my_drinks = HTML(my_drinks[['Food Name of your drinks', 'Kilocalorie amount']].to_html(classes='table table-stripped', index=False))

    if dessert != 'no':
        my_dessert = data2[data2['NDB_No'] >= 43000]
        my_dessert = my_dessert[my_dessert['NDB_No'] < 43599]
        my_dessert = my_dessert.rename(columns = {'Shrt_Desc':'Food Name of your desserts', 'Energ_Kcal':'Kilocalorie amount'})
        my_dessert = HTML(my_dessert[['Food Name of your desserts', 'Kilocalorie amount']].to_html(classes='table table-stripped', index=False))
        
    else :
        my_dessert = "You don't want any dessert"
    
    return render_template('index.html',
    cluster_text=f'You belong to cluster {cluster+1}',
    reco_text_dessert=my_dessert,  
    reco_text_drinks=my_drinks,
    button_text="Home")

if __name__ == "__main__":
    app.run(debug=True)
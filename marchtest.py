from flask import Flask, jsonify, request
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans

app = Flask(__name__)


# Predict Method goes here
diadf = pd.read_csv("diabetes.csv")
scalers = StandardScaler()
diadf_std = scalers.fit_transform(diadf.drop('Outcome', axis=1))

diapca = PCA(n_components=2)
diaprincipal_components = diapca.fit_transform(diadf_std)

principal_diadf = pd.DataFrame(data=diaprincipal_components, columns=['PC1', 'PC2'])

diakmeans = KMeans(n_clusters=2, random_state=42)
diakmeans.fit(principal_diadf)


# Load the dataset
df = pd.read_excel("ABBREV.xlsx")

# Preprocess the dataset
# Code for preprocessing goes here
df['GmWt_Desc1'] = pd.to_numeric(df['GmWt_Desc1'], errors='coerce')
df['GmWt_Desc2'] = pd.to_numeric(df['GmWt_Desc2'], errors='coerce')
df['Refuse_Pct'] = pd.to_numeric(df['Refuse_Pct'], errors='coerce')
df['Phosphorus_(mg)'] = pd.to_numeric(df['Phosphorus_(mg)'], errors='coerce')

df['Fiber_TD_(g)'] = pd.to_numeric(df['Fiber_TD_(g)'], errors='coerce')
df['FA_Sat_(g)'] = pd.to_numeric(df['FA_Sat_(g)'], errors='coerce')
df['Cholestrl_(mg)'] = pd.to_numeric(df['Cholestrl_(mg)'], errors='coerce')
df['Sodium_(mg)'] = pd.to_numeric(df['Sodium_(mg)'], errors='coerce')
df['Carbohydrt_(g)'] = pd.to_numeric(df['Carbohydrt_(g)'], errors='coerce')
df['Sugar_Tot_(g)'] = pd.to_numeric(df['Sugar_Tot_(g)'], errors='coerce')
df['Calcium_(mg)'] = pd.to_numeric(df['Calcium_(mg)'], errors='coerce')
df['Iron_(mg)'] = pd.to_numeric(df['Iron_(mg)'], errors='coerce')
df['Potassium_(mg)'] = pd.to_numeric(df['Potassium_(mg)'], errors='coerce')
df['Vit_A_IU'] = pd.to_numeric(df['Vit_A_IU'], errors='coerce')
df['Vit_C_(mg)'] = pd.to_numeric(df['Vit_C_(mg)'], errors='coerce')
df['Vit_E_(mg)'] = pd.to_numeric(df['Vit_E_(mg)'], errors='coerce')
df['Vit_D_IU'] = pd.to_numeric(df['Vit_D_IU'], errors='coerce')

df.fillna(0, inplace=True)

# Remove Outliers
#Q1 = df.quantile(0.25)
#Q3 = df.quantile(0.75)
#IQR = Q3 - Q1
#df = df[~((df < (Q1 - 1.5 * IQR)) | (df > (Q3 + 1.5 * IQR))).any(axis=1)]

# Apply Standardization
scaler = StandardScaler()
df_std = scaler.fit_transform(df.drop('Shrt_Desc', axis=1))

# Apply PCA for feature extraction
pca = PCA(n_components=2)
principal_components = pca.fit_transform(df_std)

# Convert principal components to a dataframe
principal_df = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])

# Apply K-Means clustering algorithm
kmeans = KMeans(n_clusters=3, init='k-means++', random_state=42)
kmeans.fit(principal_df)

@app.route('/sample', methods=['POST'])
def sample():
    sample_df = df[['Shrt_Desc', 'Energ_Kcal']].sample(10)
    sample_foods = sample_df.to_dict(orient='records')
    return jsonify({'sample_foods': sample_foods})

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    calorie_req = data['calorie_req']
    food_allergy = data['food_allergy']
    nutrient_req = data['nutrient_req']
    
    # Filter the dataset based on user input
    filtered_df = df[(df['Energ_Kcal'] <= float(calorie_req)) & (~df['Shrt_Desc'].str.contains(food_allergy, na=False))
                     & (df['Sugar_Tot_(g)'] <= 10) & (df['Carbohydrt_(g)'] < 55)]
    filtered_df = filtered_df.sort_values(by=[nutrient_req], ascending=False)
    # print(filtered_df.shape)

    # Apply K-Means clustering algorithm to the filtered data
    principal_components = pca.transform(filtered_df.drop('Shrt_Desc', axis=1))
    principal_df_filtered = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])
    y_kmeans = kmeans.predict(principal_df_filtered)
    principal_df_filtered['cluster'] = y_kmeans
    
    # Get recommendations for the user
    recommended_foods = []
    recommended_foods_part2 = []
    for i in range(10):
        cluster_df = principal_df_filtered[principal_df_filtered['cluster'] == i]
        cluster_foods = filtered_df[filtered_df.index.isin(cluster_df.index)]
        if len(cluster_foods) > 0:
            for j in range(len(cluster_foods)):
                recommended_foods.append(cluster_foods.iloc[j]['Shrt_Desc'])
                if len(recommended_foods) == 10:
                    break
            
        if len(recommended_foods) == 10:
            break
    
    return jsonify({'recommended_foods': recommended_foods, 
                    'recommended_foods_part2': recommended_foods_part2})

@app.route('/predict', methods=['POST'])
def predict():
    # Get user data from Android Studio app
    diadata = request.json
    pregnancies = diadata['pregnancies']
    glucose = diadata['glucose']
    blood_pressure = diadata['blood_pressure']
    skin_thickness = diadata['skin_thickness']
    insulin = diadata['insulin']
    bmi = diadata['bmi']
    diabetes_pedigree_function = diadata['diabetes_pedigree_function']
    age = diadata['age']
    
    # Filter the dataset based on user input
    user_data = [[pregnancies, glucose, blood_pressure, skin_thickness, insulin, bmi, diabetes_pedigree_function, age]]
    diadf_user = pd.DataFrame(user_data, columns=diadf.columns[:-1])
    # diadf_filtered = pd.concat([diadf_user, diadf])
    
    # Scale the data
    diadf_std = scalers.transform(diadf_user)
    user_pc = diapca.transform(diadf_std)
    user_label = diakmeans.predict(user_pc)[0]
    
    delete_my_data = 1
    if delete_my_data == 1:
        # Remove the user data
        diadf_user = diadf_user.iloc[:-1,:]
    
    # Predict cluster
    if user_label == 0:
        diabetes_result = "The user is not a type-2 diabetic patient"
    else:
        diabetes_result = "The user is a type-2 diabetic patient"
    
    # Return result to Android Studio app
    return jsonify({'diabetes_result': diabetes_result})

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)

from flask import Flask, jsonify, request
import pandas as pd
import numpy as np
import difflib
from fuzzywuzzy import fuzz
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

# Recommend Method
# Load the dataset
df = pd.read_csv("merged_abbrev_excel_csv.csv")

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

df['Descrip'] = df['Descrip'].fillna('No name')
df['FoodGroup'] = df['FoodGroup'].fillna('Hnknown Food group')
df.fillna(0, inplace=True)

# Remove Outliers
#Q1 = df.quantile(0.25)
#Q3 = df.quantile(0.75)
#IQR = Q3 - Q1
#df = df[~((df < (Q1 - 1.5 * IQR)) | (df > (Q3 + 1.5 * IQR))).any(axis=1)]

# Apply Standardization
scaler = StandardScaler()
df_std = scaler.fit_transform(df.drop(['Shrt_Desc', 'Descrip', 'FoodGroup'], axis=1))

# Apply PCA for feature extraction
pca = PCA(n_components=2)
principal_components = pca.fit_transform(df_std)

# Convert principal components to a dataframe
principal_df = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])

# Apply K-Means clustering algorithm
kmeans = KMeans(n_clusters=3, init='k-means++', random_state=42)
kmeans.fit(principal_df)


# Exercise Method
# Load the dataset
exercise_df = pd.read_csv('exercise_dataset.csv')

# Extract the relevant columns
exercise_X = exercise_df.iloc[:, 1:-1].values  # Columns 2 to 5 (weights for different lbs)
exercise_y = exercise_df.iloc[:, -1].values    # Last column (calories per lb)
activities = exercise_df.iloc[:, 0].values  # First column (activities)

# Perform k-means clustering with optimal number of clusters
exercise_n_clusters = 10  # You can set the optimal number of clusters here
exercise_kmeans = KMeans(n_clusters=exercise_n_clusters, random_state=0)
exercise_kmeans.fit(exercise_X)


# Declare the global variables
global filtered_data, nutrient_requirement, cluster_labels
filtered_data = None
nutrient_requirement = None
cluster_labels = None

@app.route('/get_activities', methods=['GET'])
def get_activities():
    # Prepare list of activities as response
    response = {'activities': activities.tolist()}
    return jsonify(response)

@app.route('/get_calories_burned', methods=['POST'])
def get_calories_burned():
    # Get input data from Android Studio
    data = request.json
    user_weight_lb = data['user_weight_lb']
    selected_activity_text = data['selected_activity']

    # Find the corresponding index of the selected activity in the activities list
    selected_activity_index = np.where(activities == selected_activity_text)[0][0]
    selected_activity = activities[selected_activity_index]

    # Predict the cluster for the user weight
    user_weight_arr = [user_weight_lb] * (exercise_X.shape[1])  # Duplicate user weight for all lb columns
    user_cluster = exercise_kmeans.predict([user_weight_arr])[0]

    # Extract the calories burned per lb for the user weight and cluster
    user_calories_per_lb = exercise_y[exercise_kmeans.labels_ == user_cluster][0]

    # Calculate the total calories burned during the selected exercise or activity for the user weight
    selected_activity_calories = exercise_y[selected_activity_index]  # Retrieve the calories per lb for the selected activity
    total_calories_burned = user_calories_per_lb * user_weight_lb * (selected_activity_calories / user_calories_per_lb)

    # Prepare response
    response = {
        'activity': selected_activity,
        'user_weight_lb': user_weight_lb,
        'total_calories_burned': total_calories_burned
    }

    return jsonify(response)

@app.route('/search', methods=['POST'])
def search():
    data = request.json
    food_search_input = data['food_search_input']

    # Replace 'No name' values in 'Descrip' column with corresponding values from 'Shrt_Desc' column
    df['Descrip'] = df.apply(lambda row: row['Shrt_Desc'] if row['Descrip'] == 'No name' else row['Descrip'], axis=1)

    # Search for the nearest word in the 'Shrt_Desc' column
    searched_foods = []
    num_results = 0

    for index, row in df.iterrows():
        if num_results == 10:
            break  # Exit the loop if 10 results are reached

        if not food_search_input:  # Check if food_search_input is empty
            searched_foods.append({
                'Shrt_Desc': row['Descrip'],
                'Energ_Kcal': int(row['Energ_Kcal']),
                'FoodGroup': row['FoodGroup']
            })
            num_results += 1
        else:
            if food_search_input.lower() in row['Descrip'].lower():  # Check if input word is in the word from 'Descrip' column
                searched_foods.append({
                    'Shrt_Desc': row['Descrip'],
                    'Energ_Kcal': int(row['Energ_Kcal']),
                    'FoodGroup': row['FoodGroup']
                })
                num_results += 1

    return jsonify({'search_food_result': searched_foods})

@app.route('/sample', methods=['POST'])
def sample():
    def replace_no_name(row):
        if row['Descrip'] == 'No name':
            return row['Shrt_Desc']
        else:
            return row['Descrip']

    sample_df = df[['Descrip', 'Shrt_Desc', 'Energ_Kcal', 'FoodGroup']].sample(10)
    sample_df['Descrip'] = sample_df.apply(replace_no_name, axis=1)
    sample_foods = sample_df.to_dict(orient='records')
    return jsonify({'sample_foods': sample_foods})

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    calorie_req = data['calorie_req']
    food_allergy = data['food_allergy']
    nutrient_req = data['nutrient_req']
    food_groups = data['food_groups']

    # Find the nearest column name from nutrient_req value
    columns = df.columns.tolist()[2:]  # exclude the first two columns
    nearest_column = difflib.get_close_matches(nutrient_req, columns, n=1, cutoff=0.3)
    if not nearest_column:
        return jsonify({'error': 'No matching nutrient column found.'}), 400
    closest_column = nearest_column[0]
    
    # Filter the dataset based on user input
    filtered_df = df[
        (df['Energ_Kcal'] <= float(calorie_req)) & 
        (~df['Descrip'].str.contains(food_allergy, na=False)) & 
        (df['Sugar_Tot_(g)'] <= 10) & 
        (df['Carbohydrt_(g)'] < 55)]
    filtered_df = filtered_df.sort_values(by=[closest_column], ascending=False)

    # Function to search for words in the food group column and merge the matches
    def search_and_merge(words):
        matches = pd.concat([cluster_foods[cluster_foods['FoodGroup'].str.contains(word, case=False)] for word in words])
        return matches

    # Function to provide suggestions for incomplete or misspelled words
    def suggest_words(word):
        suggestions = difflib.get_close_matches(word, filtered_df['FoodGroup'], n=5, cutoff=0.6)
        return suggestions

    # Apply K-Means clustering algorithm to the filtered data
    principal_components = pca.transform(filtered_df.drop(['Shrt_Desc', 'Descrip', 'FoodGroup'], axis=1))
    principal_df_filtered = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])
    y_kmeans = kmeans.predict(principal_df_filtered)
    kmeans.fit(principal_df_filtered)
    principal_df_filtered['cluster'] = y_kmeans
    
    # Get recommendations for the user
    recommended_foods = []

    for i in range(10):
        cluster_df = principal_df_filtered[principal_df_filtered['cluster'] == i]
        cluster_foods = filtered_df[filtered_df.index.isin(cluster_df.index)]
        #cluster_foods = search_and_merge(food_groups)
        if len(cluster_foods) > 0:
            for j in range(len(cluster_foods)):
                descrip = cluster_foods.iloc[j]['Descrip']
                shrt_desc = cluster_foods.iloc[j]['Shrt_Desc']
                energ_kcal = cluster_foods.iloc[j]['Energ_Kcal']
                food_group = cluster_foods.iloc[j]['FoodGroup']
                if descrip == 'No name':
                    recommended_foods.append({'descrip': shrt_desc, 'energKcal': int(energ_kcal), 'foodGroup': food_group})
                else:
                    recommended_foods.append({'descrip': descrip, 'energKcal': int(energ_kcal), 'foodGroup': food_group})
                if len(recommended_foods) == 10:
                    break
            
        if len(recommended_foods) == 10:
            break

    # Store the needed data to generate a report
    global filtered_data, nutrient_requirement, cluster_labels
    filtered_data = filtered_df
    nutrient_requirement = closest_column
    cluster_labels = kmeans.labels_
    if not recommended_foods:
        print('Empty variable')
        recommended_foods.append({'descrip': 'SERVER ERROR', 
                                  'energKcal': int(404), 
                                  'foodGroup': 'Internal malfunctions'})
    
    return jsonify({'recommended_foods': recommended_foods})

@app.route('/summary', methods=['POST'])
def summary():
    global filtered_data, nutrient_requirement, cluster_labels
    if filtered_data is not None:
        # Count the number of data points in each cluster
        cluster_counts = pd.Series(cluster_labels).value_counts()
        
        # Get the cluster with the highest count
        dominant_cluster = cluster_counts.idxmax()
        
        # Filter the dataset to get the data points in the dominant cluster
        dominant_cluster_df = filtered_data[cluster_labels == dominant_cluster]
        
        # Get the summary statistics of the dominant cluster
        summary_stats = dominant_cluster_df.describe()
        
        # Extract relevant information from the summary statistics
        num_points = summary_stats.loc['count', nutrient_requirement]
        mean_value = summary_stats.loc['mean', nutrient_requirement]
        min_value = summary_stats.loc['min', nutrient_requirement]
        max_value = summary_stats.loc['max', nutrient_requirement]
        
        # Generate a summary sentence based on the extracted information
        summary_sentence = f"In the dominant cluster, there are {num_points} food items with an average {nutrient_requirement} value of {mean_value:.2f}. The minimum {nutrient_requirement} value is {min_value:.2f} and the maximum {nutrient_requirement} value is {max_value:.2f}."

    else:

        summary_sentence = "You didn\'t run your recommendation yet."

    return jsonify({'summary_report': summary_sentence})

@app.route('/destroySummaryData', methods=['POST'])
def destroy():
    # Remove report if logout is called in android app
    global filtered_data, nutrient_requirement, cluster_labels
    if filtered_data or nutrient_requirement or cluster_labels is not None:
        filtered_data = None
        nutrient_requirement = None
        cluster_labels = None
        destroy_msg = "You have records that have been destroyed"
    else:
        destroy_msg = "No records to destroy"

    return jsonify({'destroy_msg': destroy_msg})

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

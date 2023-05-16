from flask import Flask, jsonify, request, abort
import requests
import pandas as pd
import numpy as np
import difflib
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA
from sklearn.cluster import KMeans

app = Flask(__name__)

class FoodRecommender:
    def __init__(self):
        self.df = pd.read_csv("merged_abbrev_excel_csv.csv")
        self.filtered_data = None
        self.nutrient_requirement = None
        self.cluster_labels = None
        self.scaler = None
        self.pca = None
        self.kmeans = None
        self.recommended_foods = None
        self.selected_foods = []

        # Prediction
        self.scalers = StandardScaler()
        self.diadf = pd.read_csv("diabetes.csv")
        self.diadf_std = self.scalers.fit_transform(self.diadf.drop('Outcome', axis=1))
        self.diapca = PCA(n_components=2)
        self.diaprincipal_components = self.diapca.fit_transform(self.diadf_std)
        self.principal_diadf = pd.DataFrame(data=self.diaprincipal_components, columns=['PC1', 'PC2'])
        self.diakmeans = KMeans(n_clusters=2, random_state=42, init='k-means++', n_init=10)
        self.diakmeans.fit(self.principal_diadf.values)

        # Exercise
        self.exercise_df = pd.read_csv('exercise_dataset.csv')
        self.activities = self.exercise_df.iloc[:, 0].values
        self.exercise_X = self.exercise_df.iloc[:, 1:-1].values
        self.exercise_y = self.exercise_df.iloc[:, -1].values
        self.exercise_n_clusters = 10
        self.exercise_kmeans = KMeans(n_clusters=self.exercise_n_clusters, n_init=10, random_state=0, init='k-means++')
        self.exercise_kmeans.fit(self.exercise_X)
        self.total_calories_burned = None

    def summary(self):
        if self.filtered_data is not None:
            # Count the number of data points in each cluster
            cluster_counts = pd.Series(self.cluster_labels).value_counts()
            
            # Get the cluster with the highest count
            dominant_cluster = cluster_counts.idxmax()
            
            # Filter the dataset to get the data points in the dominant cluster
            dominant_cluster_df = self.filtered_data[self.cluster_labels == dominant_cluster]
            
            # Get the summary statistics of the dominant cluster
            summary_stats = dominant_cluster_df.describe()
            
            # Extract relevant information from the summary statistics
            num_points = summary_stats.loc['count', self.nutrient_requirement]
            mean_value = summary_stats.loc['mean', self.nutrient_requirement]
            min_value = summary_stats.loc['min', self.nutrient_requirement]
            max_value = summary_stats.loc['max', self.nutrient_requirement]
            
            # Generate a summary sentence based on the extracted information
            summary_sentence = f"In the dominant cluster, there are {num_points} food items with an average {self.nutrient_requirement} value of {mean_value:.2f}. The minimum {self.nutrient_requirement} value is {min_value:.2f} and the maximum {self.nutrient_requirement} value is {max_value:.2f}."
        else:
            summary_sentence = "You didn't run your recommendation yet."

        if self.total_calories_burned is not None:
            if self.total_calories_burned > 150:
                health_monitor = "Excellent"
                recommendations = "Drink more water to regain energy"
            elif self.total_calories_burned == 0:
                health_monitor = "You haven't done your exercise today"
                recommendations = "Exercise now!"
            else:
                health_monitor = "Good"
                recommendations = "Do better exercises tomorrow"
        else:
            health_monitor = "Unidentified"
            recommendations = "Do your exercise to monitor your health"

        return jsonify({'summary_report': summary_sentence,
                        'health_monitor': health_monitor,
                        'recommendations': recommendations})

    def replace_no_name(self, row):
        if row['Descrip'] == 'No name':
            return row['Shrt_Desc']
        else:
            return row['Descrip']
        
    def get_sample_foods(self):
        sample_df = self.df[['Descrip', 'Shrt_Desc', 'Energ_Kcal', 'FoodGroup']].sample(10)
        sample_df['Descrip'] = sample_df.apply(self.replace_no_name, axis=1)
        sample_df['Energ_Kcal'] = sample_df['Energ_Kcal'].apply(lambda x: str(x) + ' KCal')
        sample_foods = sample_df.to_dict(orient='records')
        return jsonify({'sample_foods': sample_foods})

    def search_food(self, food_search_input):
        # Replace 'No name' values in 'Descrip' column with corresponding values from 'Shrt_Desc' column
        self.df['Descrip'] = self.df.apply(self.replace_no_name, axis=1)

        # Perform case-insensitive search using str.contains method
        searched_df = self.df[self.df['Descrip'].str.contains(food_search_input, case=False)]

        searched_foods = []
        num_results = 0

        for index, row in searched_df.iterrows():
            if num_results == 10:
                break  # Exit the loop if 10 results are reached

            searched_foods.append({
            'Shrt_Desc': row['Descrip'],
            'Energ_Kcal': str(row['Energ_Kcal']) + " KCal",
            'FoodGroup': row['FoodGroup']
            })
            num_results += 1

        return searched_foods

    def get_activities(self):
        return self.activities.tolist()

    def get_calories_burned(self, user_weight_lb, selected_activity_text):
        try:
            selected_activity_index = np.where(self.activities == selected_activity_text)[0][0]
            selected_activity = self.activities[selected_activity_index]

            user_weight_arr = [user_weight_lb] * (self.exercise_X.shape[1])
            user_cluster = self.exercise_kmeans.predict([user_weight_arr])[0]

            user_calories_per_lb = self.exercise_y[self.exercise_kmeans.labels_ == user_cluster][0]

            selected_activity_calories = self.exercise_y[selected_activity_index]
            total_calories_burned = user_calories_per_lb * user_weight_lb * (selected_activity_calories / user_calories_per_lb)
            self.total_calories_burned = total_calories_burned

            return selected_activity, user_weight_lb, total_calories_burned
        except Exception as e:
            print(f"Error: {str(e)}")
        

    def predict(self, diadata):
        pregnancies = diadata['pregnancies']
        glucose = diadata['glucose']
        blood_pressure = diadata['blood_pressure']
        skin_thickness = diadata['skin_thickness']
        insulin = diadata['insulin']
        bmi = diadata['bmi']
        diabetes_pedigree_function = diadata['diabetes_pedigree_function']
        age = diadata['age']
        
        user_data = [[pregnancies, glucose, blood_pressure, skin_thickness, insulin, bmi, diabetes_pedigree_function, age]]
        diadf_user = pd.DataFrame(user_data, columns=self.diadf.columns[:-1])
        diadf_std = self.scalers.transform(diadf_user)
        user_pc = self.diapca.transform(diadf_std)
        user_label = self.diakmeans.predict(user_pc)[0]
        
        if user_label == 0:
            diabetes_result = "You are not diagnosed with Type-2."
        else:
            diabetes_result = "You are diagnosed with Type-2."
        
        return diabetes_result

    def preprocess_data(self):
        numeric_cols = ['GmWt_Desc1', 'GmWt_Desc2', 'Refuse_Pct', 'Phosphorus_(mg)', 'Fiber_TD_(g)',
                        'FA_Sat_(g)', 'Cholestrl_(mg)', 'Sodium_(mg)', 'Carbohydrt_(g)', 'Sugar_Tot_(g)',
                        'Calcium_(mg)', 'Iron_(mg)', 'Potassium_(mg)', 'Vit_A_IU', 'Vit_C_(mg)', 'Vit_E_(mg)',
                        'Vit_D_IU']
        self.df[numeric_cols] = self.df[numeric_cols].apply(pd.to_numeric, errors='coerce')
        self.df['Descrip'] = self.df['Descrip'].fillna('No name')
        self.df['FoodGroup'] = self.df['FoodGroup'].fillna('Unknown Food group')
        self.df.fillna(0, inplace=True)
        self.scaler = StandardScaler()
        df_std = self.scaler.fit_transform(self.df.drop(['Shrt_Desc', 'Descrip', 'FoodGroup'], axis=1))
        self.pca = PCA(n_components=2)
        self.pca.fit(df_std)
        principal_components = self.pca.transform(df_std)
        self.principal_df = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])
        self.kmeans = KMeans(n_clusters=3, n_init=10, init='k-means++', random_state=42)
        self.kmeans.fit(self.principal_df)

    def get_nearest_column(self, nutrient_req):
        columns = self.df.columns.tolist()[2:]
        nearest_column = difflib.get_close_matches(nutrient_req, columns, n=1, cutoff=0.3)
        if not nearest_column:
            return None
        else:
            return nearest_column[0]

    def filter_recommendations(self, calorie_req, food_allergy, nutrient_req):
        closest_column = self.get_nearest_column(nutrient_req)
        if not closest_column:
            return []

        filtered_df = self.df[
            (self.df['Energ_Kcal'] <= float(calorie_req)) &
            (~self.df['Descrip'].str.contains(food_allergy, na=False)) &
            (self.df['Sugar_Tot_(g)'] <= 10) & (self.df['Carbohydrt_(g)'] < 55)
        ]
        filtered_df = filtered_df.sort_values(by=[closest_column], ascending=False)

        principal_components = self.pca.transform(self.scaler.transform(filtered_df.drop(['Shrt_Desc', 'Descrip', 'FoodGroup'], axis=1)))
        principal_df_filtered = pd.DataFrame(data=principal_components, columns=['PC1', 'PC2'])
        y_kmeans = self.kmeans.predict(principal_df_filtered)
        principal_df_filtered['cluster'] = self.kmeans.predict(principal_df_filtered)

        principal_df_filtered['cluster'] = principal_df_filtered['cluster'].sample(20)
        recommended_foods = []

        for i in range(20):  # Update the range to include the first 20 foods
            cluster_df = principal_df_filtered[principal_df_filtered['cluster'] == i]
            cluster_foods = filtered_df[filtered_df.index.isin(cluster_df.index)]
            if len(cluster_foods) > 0:
                for j in range(len(cluster_foods)):
                    descrip = cluster_foods.iloc[j]['Descrip']
                    shrt_desc = cluster_foods.iloc[j]['Shrt_Desc']
                    energ_kcal = cluster_foods.iloc[j]['Energ_Kcal']
                    food_group = cluster_foods.iloc[j]['FoodGroup']
                    food = {
                        'descrip': shrt_desc if descrip == 'No name' else descrip,
                        'energKcal': str(energ_kcal) + ' Kcal',
                        'foodGroup': food_group
                    }
                    recommended_foods.append(food)
                    if len(recommended_foods) == 10:
                        break
            if len(recommended_foods) == 10:
                break

        self.recommended_foods = recommended_foods
        self.filtered_data = filtered_df
        self.nutrient_requirement = closest_column
        self.cluster_labels = y_kmeans

        return recommended_foods
    
    def recommend_again(self, selected_food):
        # Filter the dataset based on the selected food
        selected_food_df = self.df[self.df['FoodGroup'] == selected_food]
        
        # Filter the dataset again based on the nutrient requirement and selected food
        filtered_again_df = selected_food_df[selected_food_df[self.nutrient_requirement] > 0]
        filtered_again_df = filtered_again_df.sort_values(by=[self.nutrient_requirement], ascending=False)
        
        # Get a list of food descriptions from the existing recommendations
        recommended_food_descriptions = [food['descrip'] for food in self.recommended_foods]

        # Get 3 random foods from the filtered dataset, excluding the ones already recommended
        recommended_foods_again = []
        for i in range(3):  # Update the range to include the desired number of foods
            filtered_food = None
            while filtered_food is None:
                if len(filtered_again_df) == 0:
                    break
                row = filtered_again_df.sample(n=1).iloc[0]
                if row['Shrt_Desc'] not in recommended_food_descriptions:
                    filtered_food = {
                        'descrip': row['Shrt_Desc'],
                        'energKcal': str(row['Energ_Kcal']) + ' Kcal',
                        'foodGroup': row['FoodGroup']
                    }
            if filtered_food:
                recommended_foods_again.append(filtered_food)
        
        return jsonify({'recommended_foods_again': recommended_foods_again})
    
    def store_selected_foods(self, selected_food):
        # Check if the selected food already exists in self.selected_foods
        if selected_food not in self.selected_foods:
            # Add the selected food to the list of selected foods
            self.selected_foods.append(selected_food)

    def get_selected_foods(self):
        # Create a new endpoint to fetch the selected foods and specific values
        selected_foods_data = []
        for value in self.selected_foods:
            matching_rows = self.df[self.df['Descrip'] == value]
            if not matching_rows.empty:
                food = matching_rows.iloc[0]  # Get the first matching row
                selected_foods_data.append({
                    'descrip': food['Descrip'],
                    'energKcal': str(food['Energ_Kcal']) + ' Kcal',
                    'foodGroup': food['FoodGroup']
                })
        return jsonify({'selected_foods': selected_foods_data})
    
    def get_food_value(self, food_name):
        matching_row = self.df.loc[self.df['Descrip'] == food_name]

        if matching_row.empty:
            matching_row = self.df.loc[self.df['Shrt_Desc'] == food_name]

        if matching_row.empty:
            return None

        selected_food = matching_row.to_dict(orient='records')[0]

        # Add column names as titles to each value
        selected_food_with_titles = {column: value for column, value in selected_food.items()}
        return jsonify({'values': selected_food_with_titles})


@app.route('/summary', methods=['POST', 'GET'])
def summary():
    return food_recommender.summary()

@app.route('/sample', methods=['POST', 'GET'])
def sample():
    return food_recommender.get_sample_foods()

@app.route('/search', methods=['POST', 'GET'])
def search():
    data = request.json
    food_search_input = data['food_search_input']

    result = food_recommender.search_food(food_search_input)

    return jsonify({'search_food_result': result})

@app.route('/get_activities', methods=['GET'])
def get_activities():
    activities = food_recommender.get_activities()
    response = {'activities': activities}
    return jsonify(response)

@app.route('/get_calories_burned', methods=['POST', 'GET'])
def get_calories_burned():
    data = request.json
    user_weight_lb = float(data['user_weight_lb'])
    selected_activity_text = data['selected_activity']

    selected_activity, user_weight_lb, total_calories_burned = food_recommender.get_calories_burned(user_weight_lb, selected_activity_text)

    response = {
        'activity': selected_activity,
        'user_weight_lb': f"Your weight is {user_weight_lb:.2f} lbs",
        'total_calories_burned': f"You have burned a total of {total_calories_burned:.2f} calories with just {selected_activity} activity."
    }

    return jsonify(response)

@app.route('/predict', methods=['POST'])
def predict():
    diadata = request.json
    result = food_recommender.predict(diadata)
    return jsonify({'diabetes_result': result})

@app.route('/recommend', methods=['POST', 'GET'])
def recommend():
    data = request.json
    calorie_req = data['calorie_req']
    food_allergy = data['food_allergy']
    nutrient_req = data['nutrient_req']
    #food_groups = data['food_groups']

    recommended_foods = food_recommender.filter_recommendations(calorie_req, food_allergy, nutrient_req)

    if recommended_foods:
        return jsonify({'recommended_foods': recommended_foods})
    else:
        abort(404, 'No recommendations found')

@app.route('/recommend_again', methods=['POST'])
def recommend_again():
    selected_food = request.json.get('selected_food')
    selected_food_name = request.json.get('selected_food_name')
    if selected_food and selected_food_name:
        recommended_foods_again = food_recommender.recommend_again(selected_food)
        food_recommender.store_selected_foods(selected_food_name)
        return recommended_foods_again
    else:
        return jsonify({'error': 'Invalid request'})
    
@app.route('/get_selected_foods', methods=['GET'])
def get_selected_foods():
    return food_recommender.get_selected_foods()

@app.route('/get_food_value', methods=['POST', 'GET'])
def get_food_value():
    food_name = request.json.get('selected_food_name')
    selected_food = food_recommender.get_food_value(food_name)
    
    if selected_food is None:
        return jsonify({'error': 'Food not found'})
    
    return selected_food

if __name__ == '__main__':
    food_recommender = FoodRecommender()
    food_recommender.preprocess_data()
    app.run(debug=True, host='0.0.0.0', port=5000)
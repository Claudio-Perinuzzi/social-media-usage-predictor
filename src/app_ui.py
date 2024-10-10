from googlesearch import search
from bs4 import BeautifulSoup
import streamlit as st
import subprocess
import requests
import os

###################################################################
# UI for streamlit app
# Contains:
#   - User defined inputs
#   - Buttons
#   - Running the Jar file
#   - Web searching based on the user's interests/hobbies
###################################################################


user_values = {
    # Custom values used to help the user
    'to_serialize': None,   # args[0] Boolean (true if the user wants to load in a existent model)
    'state_town': None,     # State and Town of User 
    'hobbies': None,        # Hobbies used for healthy habits
    
    # Tree values used for the prediction 
    'age': None,
    'gender': None,
    'time_spent': None,
    'platform': None, 
    'interests': None, 
    'location': None,       # Country of User   
    'demographics': None,
    'profession': None,
    'income': None,
    'indebt': None,
    'isHomeOwner': None,
    'Owns_Car': None
}


def get_age():
    user_age = st.number_input('Enter your age', min_value=1, max_value=100)
    user_age = str(user_age)
    user_values['age'] = user_age


def get_gender():
    user_gender = st.selectbox('Choose your gender', ['Male', 'Female'])
    user_values['gender'] = user_gender.lower()


def get_time_spent():
    user_time_spent = st.number_input('Enter the number of hours you use social media per day', min_value=0, max_value=24)
    user_time_spent = str(user_time_spent)
    user_values['time_spent'] = user_time_spent


def get_platform():
    platforms = ['Instagram', 'YouTube', 'Facebook']
    user_platform = st.selectbox('Choose your most favorite social media platform', platforms)
    user_values['platform'] = user_platform


def get_interest():
    interests = ['Lifestyle', 'Sports', 'Travel']
    user_interests = st.selectbox('Choose your most favorite interest', interests)
    user_values['interests'] = user_interests


def get_hobbies():
    user_hobbies = st.text_input("Enter your favorite hobby.")
    user_values['hobbies'] = user_hobbies


def get_country():
    locations = ['Australia', 'United Kingdom', 'United States', 'None']
    user_location = st.selectbox('Choose the location you live in', locations)
    if user_location == 'None':
        user_location = st.text_input('Please manually enter the country you reside in')
    user_values['location'] = user_location

    
def get_state_town():
    state_town = st.text_input('Please enter the state and town where you reside')
    user_values['state_town'] = state_town


def get_demographics():
    demographics = ['Rural', 'Suburban', 'Urban']
    user_demographics = st.selectbox('Choose your locations demographics', demographics)
    if user_demographics == 'Suburban': user_demographics = 'Sub_Urban'
    user_values['demographics'] = user_demographics


def get_profession():
    professions = ['Marketer Manager', 'Software Engineer', 'Student', 'Neither']
    user_profession = st.selectbox('Choose your profession', professions)
    user_values['profession'] = user_profession


def get_income():
    user_income = st.number_input('Enter your income', min_value=0, max_value=1_000_000)
    user_values['income'] = str(user_income)


def get_indebt():
    user_indebt = st.selectbox('Are you indebt?', ['Yes', 'No'])
    user_indebt = 'True' if user_indebt == 'Yes' else 'False'
    user_values['indebt'] = user_indebt


def get_home_owner():
    user_home_owner = st.selectbox('Are you a homeowner?', ['Yes', 'No'])
    user_home_owner = 'True' if user_home_owner == 'Yes' else 'False'
    user_values['isHomeOwner'] = user_home_owner


def get_car_owner():
    user_owns_car = st.selectbox('Do you own a car?', ['Yes', 'No'])
    user_owns_car = 'True' if user_owns_car == 'Yes' else 'False'
    user_values['Owns_Car'] = user_owns_car


# Trains a New Model and gets the predictions
def train_new_model():
    if st.button('Train a New Model (Recommended)'):
        user_values['to_serialize'] = 'False'
        prediction = run_jar(user_values)
        st.text_area("Prediction:", prediction, height=100)
        suggestions = offer_suggestions(user_values)
        display_links(prediction, suggestions)


# Loads in a Existent Model and gets the prediction
def load_existent_model():
    if st.button('Load in a Existent Model'):
        user_values['to_serialize'] = 'True'
        prediction = run_jar(user_values)
        st.text_area("Prediction:", prediction, height=100)
        suggestions = offer_suggestions(user_values)
        display_links(prediction, suggestions)


# Displays the links to the user
def display_links(prediction, suggestions):
    if 'risk' in prediction:
        st.write("Here are some suggested links based on your interests/hobbies that may help with your social media usage!")
    else:
        st.write("Keep up the good work! Here are some links to help encourage you to maintain healthy habits!")

    for title, link in suggestions:
        st.markdown(link) 


# Runs the .jar file to get the prediction
def run_jar(arg_map):
    try:
        # Construct the absolute path for the .jar file
        jar_path = os.path.abspath('dist/predict.jar')
        
        if not os.path.exists(jar_path):
            return "Error: predict.jar file not found!"        
        
        # Extract the values from the dictionary and convert them to strings
        string_args = [str(value) for value in arg_map.values()]

        # Run the .jar file with the provided arguments
        result = subprocess.run(['java', '-jar', jar_path] + string_args, capture_output=True, text=True)

        return result.stdout if result.stdout else result.stderr
    
    except Exception as e:
        return str(e)  
    

def calculate_miles(owns_car):
    return 50 if owns_car == 'True' else 10


def offer_suggestions(user_values):

    # List of suggestions (links from queries)
    suggestions = []

    # User values we are interested in
    radius_miles = calculate_miles(user_values['Owns_Car']) # Calculate how far the user can go (10 miles if no car, 50 with a car)
    country      = user_values['location']      # Look in this country
    state_town   = user_values['state_town']    # Look in the radius from this town
    interests    = user_values['interests']     # Look for interests
    hobbies      = user_values['hobbies']       # Look for hobbies
    indebt       = user_values['indebt']        # Offer cheaper options

    # Query the results
    if indebt == 'True':
        query = f"inexpensive {interests} activities near {state_town}, {country} within {radius_miles} miles"
        search_for_results(suggestions, query)
    else:
        query = f"{interests} activities near {state_town}, {country} within {radius_miles} miles"   
        search_for_results(suggestions, query)

    if hobbies:
        query = f"{hobbies} near {state_town}, {country} within {radius_miles} miles"
        search_for_results(suggestions, query)

    return suggestions


# Get 3 links for the query
def search_for_results(suggestions, query):
    search_results = search(query, num_results=3)
    for result in search_results:
        try:
            page = requests.get(result)
            soup = BeautifulSoup(page.content, 'html.parser')
            title = soup.title.string
            suggestions.append((title, result))
        except Exception as e:
            print(f"Error processing {result}: {e}")

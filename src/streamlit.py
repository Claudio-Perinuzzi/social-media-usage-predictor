import streamlit as st
import subprocess
import os

# TODO
    # Truncate decimal off confidence level
    # ADDING ADDLT INFO FOR HELPING USER W/ INFO
    # Change location to text box, can be anything
    # Change interets to text box, can be anything
    # If owns car & travel, radius 50 miles
    # if indebt, offer cheaper options
    # if homeOwner, offer tech free rooms options
    # if homeOwner and demographics is suburban, rural, offer options there
    # if homeOwner and urban, offer options there         

def main():

    user_values = {
        # Custom values used to help the user
        'to_serialize': None,   # args[0] Boolean (true if the user wants to load in a existent model)
        'state_town': None,     # State and Town of User    
        'hobbies': None,               
        
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


    # Get Age
    user_age = st.number_input('Enter your age', min_value=1, max_value=100)
    user_age = str(user_age)
    user_values['age'] = user_age


    # Get Gender
    user_gender = st.selectbox('Choose your gender', ['Male', 'Female'])
    user_values['gender'] = user_gender.lower()


    # Get Time Spent
    user_time_spent = st.number_input('Enter the number of hours you use social media per day', min_value=0, max_value=24)
    user_time_spent = str(user_time_spent)
    user_values['time_spent'] = user_time_spent


    # Get perferred platform
    platforms = ['Instagram', 'YouTube', 'Facebook']
    user_platform = st.selectbox('Choose your most favorite social media platform', platforms)
    user_values['platform'] = user_platform


    # Get favorite interest
    interests = ['Lifestlye', 'Sports', 'Travel']
    user_interests = st.selectbox('Choose your most favorite interest', interests)
    user_values['interests'] = user_interests


    # Get Hobbies
    user_hobbies = st.text_input("Enter your favorite hobby.")
    user_values['hobbies'] = user_hobbies


    # Get User's Location (Country)
    locations = ['Australia', 'United Kingdom', 'United States', 'None']
    user_location = st.selectbox('Choose the location you live in', locations)
    if user_location == 'None':
        user_location = st.text_input('Please manually enter the country you reside in')
    user_values['location'] = user_location

    
    #Get User's State/Town
    state_town = st.text_input('Please enter the state and town where you reside')
    user_values['state_town'] = state_town


    # Get Demographics
    demographics = ['Rural', 'Suburban', 'Urban']
    user_demographics = st.selectbox('Choose your locations demographics', demographics)
    if user_demographics == 'Suburban': user_demographics = 'Sub_Urban'
    user_values['demographics'] = user_demographics


    # Get Profession
    professions = ['Marketer Manager', 'Software Engineer', 'Student', 'Neither']
    user_profession = st.selectbox('Choose your profession', professions)
    user_values['profession'] = user_profession


    # Get Income
    user_income = st.number_input('Enter your income', min_value=0, max_value=1_000_000)
    user_values['income'] = str(user_income)


    # Get If Indebt
    user_indebt = st.selectbox('Are you indebt?', ['Yes', 'No'])
    user_indebt = 'True' if user_indebt == 'Yes' else 'False'
    user_values['indebt'] = user_indebt


    # Get If Homeowner
    user_home_owner = st.selectbox('Are you a homeownder?', ['Yes', 'No'])
    user_home_owner = 'True' if user_home_owner == 'Yes' else 'False'
    user_values['isHomeOwner'] = user_home_owner


    # Get If Car Owner
    user_owns_car = st.selectbox('Do you own a car?', ['Yes', 'No'])
    user_owns_car = 'True' if user_owns_car == 'Yes' else 'False'
    user_values['Owns_Car'] = user_owns_car


    # Train a Model Button
    st.title("Get Your Prediciton")
    prediction = ''


    # Trains a new Model Button
    if st.button('Train a New Model (Recommended)', 'am i another button?'):
        user_values['to_serialize'] = 'False'
        prediction = run_jar(user_values)
        st.text_area("Prediction:", prediction, height=300)


    # Load in a Existent Model Button
    if st.button('Load in a Existent Model'):
        user_values['to_serialize'] = 'True'
        prediction = run_jar(user_values)
        st.text_area("Prediction", prediction, height=300)


    # Offer suggestions to help the user, if the user is at risk of social medial addiction
    if 'risk' in prediction:
        offerSuggestions(user_values)
    else:
        pass #TODO 



# Function to run the .jar file
def run_jar(arg_map):
    try:
        # Construct the absolute path for the .jar file
        jar_path = os.path.abspath('dist/predict.jar')
        print(jar_path)
        
        # Check if the .jar file exists
        if not os.path.exists(jar_path):
            return "Error: predict.jar file not found!"        
        
        # Extract the values from the dictionary and convert them to strings
        string_args = [str(value) for value in arg_map.values()]

        # Run the .jar file with the provided arguments
        result = subprocess.run(['java', '-jar', jar_path] + string_args, capture_output=True, text=True)

        # Debugging: Print both stdout and stderr to help identify any issues
        print("STDOUT (JAR):", result.stdout)
        print("STDERR (JAR):", result.stderr)

        # Return the output from the JAR execution
        return result.stdout if result.stdout else result.stderr
    except Exception as e:
        return str(e)  # Return any error messages if something goes wrong


# TODO ########
# If owns car & travel, radius 50 miles
# if indebt, offer cheaper options
# if homeOwner, offer tech free rooms options
# if homeOwner and demographics is suburban, rural, offer options there
# if homeOwner and urban, offer options there         
def offerSuggestions(user_values):
    radius_miles = calculate_miles(user_values['Owns_Car']) # Calculate how far the user can go (10 miles if no car, 50 with a car)
    country = user_values['location']
    state_town = user_values['state_town']
    interests = user_values['interests']
    hobbies = user_values['hobbies']
    
    if user_values['interests'] == 'Travel':
        pass



def calculate_miles(owns_car):
    return 50 if owns_car == 'True' else 10



if __name__ == '__main__':
    main()



# #   user_values = {
#     # Custom values used to help the user
#     'to_serialize': None,   # args[0] Boolean (true if the user wants to load in a existent model)
#     'state_town': None,     # State and Town of User    
#     'hobbies': None,               
    
#     # Tree values used for the prediction 
#     'age': None,
#     'gender': None,
#     'time_spent': None,
#     'platform': None, 
#     'interests': None, 
#     'location': None,       # Country of User   
#     'demographics': None,
#     'profession': None,
#     'income': None,
#     'indebt': None,
#     'isHomeOwner': None,
#     'Owns_Car': None
# }
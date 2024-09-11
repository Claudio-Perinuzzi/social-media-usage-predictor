import streamlit as st
import subprocess
import os


def main():


    user_values = {
        'age': None,
        'gender': None,
        'time_spent': None,
        'platform': None, 
        'interests': None, # Any text option
        'location': None,  # Any text option
        'demographics': None,
        'profession': None,
        'income': None,
        'indebt': None,
        'isHomeOwner': None,
        'Owns_Car': None,      

        # ADDING ADDLT INFO FOR HELPING USER W/ INFO
        # Change location to text box, can be anything
        # Change interets to text box, can be anything
        # If owns car & travel, radius 50 miles
        # if indebt, offer cheaper options
        # if homeOwner, offer tech free rooms options
        # if homeOwner and demographics is suburban, rural, offer options there
        # if homeOwner and urban, offer options there 
        
        # Add more
        'to_serialze': None  
    }

    user_age = st.number_input('Enter your age', min_value=1, max_value=100)
    user_age = str(user_age)
    user_values['age'] = user_age

    user_gender = st.selectbox('Choose your gender', ['Male', 'Female'])
    user_values['gender'] = user_gender.lower()

    user_time_spent = st.number_input('Enter the number of hours you use social media per day', min_value=0, max_value=24)
    user_time_spent = str(user_time_spent)
    user_values['time_spent'] = user_time_spent

    platforms = ['Instagram', 'YouTube', 'Facebook']
    user_platform = st.selectbox('Choose your most favorite social media platform', platforms)
    user_values['platform'] = user_platform

    interests = ['Lifestlye', 'Sports', 'Travel']
    user_interests = st.selectbox('Choose your most favorite interest', interests)
    user_values['interests'] = user_interests

    locations = ['Australia', 'United Kingdom', 'United States']
    user_location = st.selectbox('Choose the location you live in', locations)
    user_values['location'] = user_location

    demographics = ['Rural', 'Suburban', 'Urban']
    user_demographics = st.selectbox('Choose your locations demographics', demographics)
    if user_demographics == 'Suburban': user_demographics = 'Sub_Urban'
    user_values['demographics'] = user_demographics

    professions = ['Marketer Manager', 'Software Engineer', 'Student']
    user_profession = st.selectbox('Choose your profession', professions)
    user_values['profession'] = user_profession

    user_income = st.number_input('Enter your income', min_value=0, max_value=1_000_000)
    user_values['income'] = str(user_income)

    user_indebt = st.radio('Are you indebt?', ['Yes', 'No'])
    user_indebt = 'True' if user_indebt == 'Yes' else 'False'
    user_values['indebt'] = user_indebt

    user_home_owner = st.radio('Are you a homeownder?', ['Yes', 'No'])
    user_home_owner = 'True' if user_home_owner == 'Yes' else 'False'
    user_values['isHomeOwner'] = user_home_owner

    user_owns_car = st.radio('Do you own a car?', ['Yes', 'No'])
    user_owns_car = 'True' if user_owns_car == 'Yes' else 'False'
    user_values['Owns_Car'] = user_owns_car





    # Streamlit UI
    st.title("Run JAR File from Streamlit")

    # Button to trigger the jar execution
    if st.button("Run JAR"):
        output = run_jar(user_values)
        st.text_area("Output", output, height=300)





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

if __name__ == '__main__':
    main()
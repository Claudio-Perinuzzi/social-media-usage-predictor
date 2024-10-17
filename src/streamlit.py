import streamlit as st
from app_ui import *

import matplotlib.pyplot as plt
    
###################################################################
# Streamlit Main Logic:
#   - Gets user defined values from app_ui.py
#   - Trains or loads in a model to get a prediction
#   - Displays the results of the prediction along with links
#     for developing and/or maintaining healthy habits
###################################################################


def main():

    display_heading()

    # Get user info for the random forest model
    get_age()
    get_gender()
    get_time_spent()
    get_platform()
    get_interest()
    get_hobbies()
    get_country()
    get_state_town()
    get_demographics()
    get_profession()
    get_income()
    get_indebt()
    get_home_owner()
    get_car_owner()

    st.title("Get Your Prediction")

    train_new_model()
    load_existent_model()

    display_end()


if __name__ == '__main__':
    main()
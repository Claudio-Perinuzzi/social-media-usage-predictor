# Social Media Usage Predictor

## Usage and Interaction

the Social Media Usage Predictor is a web application that predicts social media addiction based on user-provided information, including social media habits and socioeconomic background.

You can try out the app live here: [Social Meia Usafe Predictor App](https://social-media-usage-predictor.streamlit.app/)

## Introduction

In today’s age, social media consumption has been rapidly increasing as companies vie for user attention. This competition for user attention has caused social media companies to employ new features designed to retain users on their platform for longer. One recent example was the emergence of Tik-Tok, a social media platform that became popular for its main feature that plays short video clips with each swipe of the finger. This simple feature takes advantage of a user’s small attention span to try and keep them on the platform for longer. This feature was so effective that other companies such as Youtube and Instagram have implemented their own versions of this feature. As a result, social media consumption has skyrocketed and the absence of regulations in this area causes concern for a user’s well-being. 

Our system is designed to combat these predatory features indirectly by providing the user with a prediction on how likely the user is to spend an excessive amount of time on social media. The system will also provide the user with customized recommendations and local resources so that the user can make informed decisions about their social media usage. Our system is specifically designed for individual users who wish to develop healthier habits. 


## System and Random Forest Modeling Diagrams

The high level architectural design of the system is as follows:

![MVC Architecture](assets/uml_diagram1.png)









## Linear Regression (Prediction Model)

This project involves predicting future Air Quality Index (AQI) averages using a linear regression model. The model is trained on historical annual and seasonal AQI averages, categorized by borough community district codes. This approach allows us to forecast AQI values for each borough code based on historical trends.

### Model Training and Prediction

- **Data**: The model uses AQI data over multiple years, segmented by borough community district codes. The dataset includes both annual and seasonal averages.
  
- **Training**: The linear regression model was trained on the historical AQI data. The downward trend observed in the dataset helps the model make informed predictions.

- **Prediction Horizon**: Due to the ongoing downward trend, the model can reliably predict AQI averages up to the next 5 years. Beyond this period, accuracy may decline as the dataset does not show a tapering off in AQI values.

### Visualization

A scatter plot illustrating the downward trend for Manhattan's borough codes is shown below. This visualization helps to understand the trend and validate the model's predictions.

![Scatter Plot of AQI Trends](assets/scatter_plots/annual_scatter_plot_boro_cd_100s.png)

### Model Performance

The model's performance is evaluated based on its ability to predict future AQI values accurately. As the dataset shows a consistent trend without a tapering effect, predictions remain reliable within the next 5 years.

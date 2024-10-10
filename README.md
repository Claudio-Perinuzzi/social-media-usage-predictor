# Social Media Usage Predictor

## Usage and Interaction

the Social Media Usage Predictor is a web application that predicts social media addiction based on user-provided information, including social media habits and socioeconomic background.

You can try out the app live here: [Social Meia Usage Predictor App](https://social-media-usage-predictor.streamlit.app/)

## Introduction

In today’s age, social media consumption has been rapidly increasing as companies vie for user attention. This competition for user attention has caused social media companies to employ new features designed to retain users on their platform for longer. One recent example was the emergence of Tik-Tok, a social media platform that became popular for its main feature that plays short video clips with each swipe of the finger. This simple feature takes advantage of a user’s small attention span to try and keep them on the platform for longer. This feature was so effective that other companies such as Youtube and Instagram have implemented their own versions of this feature. As a result, social media consumption has skyrocketed and the absence of regulations in this area causes concern for a user’s well-being. 

Our system is designed to combat these predatory features indirectly by providing the user with a prediction on how likely the user is to spend an excessive amount of time on social media. The system will also provide the user with customized recommendations and local resources so that the user can make informed decisions about their social media usage. Our system is specifically designed for individual users who wish to develop healthier habits. 


## System and Random Forest Modeling Diagrams

The high level architectural design of the system is as follows:

![MVC Architecture](assets/uml_diagram1.png)

Training the Random Forest:

![Random Forest Training](assets/uml_sequence_functional1.png)

Reading in User Data and Predicting:

![Prediction On User Data](assets/uml_sequence_functional2.png)

Generating a Report:

![Report](assets/uml_sequence_functional3.png)

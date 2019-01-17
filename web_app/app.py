import requests
from flask import Flask, render_template, redirect, request

app = Flask(__name__)

@app.route('/')
def main():
    return redirect('/index')


@app.route('/index')
def index():
    return render_template('index.html')


@app.route('/display', methods=['GET', 'POST'])
def display():    

    # ––––––––––––––––––– Power On/Off ––––––––––––––––––––– #

    light_on = request.form['Power']

    power_url = 'http://127.0.0.1:4567/light/%s' % light_on


    # ––––––––––––– Get requested city from user –––––––––––– #
    requested_city = request.form['City']

    requested_url = 'http://127.0.0.1:4567/city/%s' % requested_city 


    # ––––––––––– Get current weather information ––––––––––– #

    # Gets the API data of the specified city
    r = requests.get(requested_url).json()

    # If statements to attribute an icon depending on
    # the main weather and whether it's day or night
    if r['mainWeather'] == 'Thunderstorm':
        icon = 'thunderstorm.svg'

    if r['mainWeather'] == 'Drizzle':
        if r['day'] == True:
            icon = 'drizzle-day.svg'
        else:
            icon = 'drizzle-night.svg'

    if r['mainWeather'] == 'Rain':
        icon = 'rain.svg'

    if r['mainWeather'] == 'Snow':
        icon = 'snow.svg'

    if r['mainWeather'] == 'Atmosphere' or 'Mist':
        icon = 'atmosphere.svg'

    if r['mainWeather'] == 'Clear':
        if r['day'] == True:
            icon = 'clear-day.svg'
        else:
            icon = 'clear-night.svg'

    if r['mainWeather'] == 'Clouds':
        if r['day'] == True:
            icon = 'clouds-day.svg'
        else:
            icon = 'clouds-night.svg'


    # –––– Determining the Python weather object ––––– #
    weather = {
        'city' : requested_city,
        'main' : r['mainWeather'],
        'description' : r['details'],
        'temperature' : r['temperature'],
        'humidity' : r['humidity'],
        'speed' : r['windSpeed'],
        'icon' : icon
    }

    print("API: ", r)
    print("LOCAL DATA:", weather)
    return render_template('display.html', weather=weather)


@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404


@app.errorhandler(500)
def page_not_found(e):
    return render_template('500.html'), 500

if __name__ == '__main__':
    app.debug = True

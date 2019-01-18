# Installation



# climeXhue web app

This is the user interface for the climeXhue API, allowing the user to set a Philips Hue lamp to a certain city and shining with a color depending on the weather of that city.

## Installation

1. Start a virtual engine inside the `web_app` folder, e.g. with the following prompts:
```bash
$ python3 -m venv venv
$ source venv/bin/activate
```
2. Download the required packages for the app, `flask` and `requests`, using the package manager [pip](https://pip.pypa.io/en/stable/).
```bash
$ pip install flask
$ pip install requests
```
3. Export the Flask app
```bash
$ export FLASK_APP=app.py
```
4. Run the Flask app and open in your browser
```bash
$ python -m flask run
 * Running on http://127.0.0.1:5000/
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)

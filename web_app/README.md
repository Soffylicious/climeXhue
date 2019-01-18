

1. Go to the "web_app" folder 
2. Start a virtual engine, for example with the following prompts:
```
$ python3 -m venv venv
$ source venv/bin/activate
```
3. Download flask and requests
```
$ pip install flask
$ pip install requests
```
4. Export the Flask app and run
```
$ export FLASK_APP=app.py
$ python -m flask run
```
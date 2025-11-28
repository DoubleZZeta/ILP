from dashboard import create_app

app = create_app()

if __name__ == '__main__':
    # Local development server
    app.run(host='127.0.0.1', port=5001, debug=False, use_reloader=False)

from flask import Flask
from pathlib import Path

def create_app(config=None):
    # Get absolute paths to templates and static folders
    dashboard_dir = Path(__file__).parent
    app = Flask(__name__, 
                template_folder=str(dashboard_dir / 'templates'), 
                static_folder=str(dashboard_dir / 'static'))
    if config:
        app.config.update(config)

    # Import and register blueprint
    from .views import bp as main_bp
    app.register_blueprint(main_bp)

    return app

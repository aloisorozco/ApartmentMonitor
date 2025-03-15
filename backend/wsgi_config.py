import sys
path = '/home/appartmonitor/ApartmentMonitor/backend'
if path not in sys.path:
    sys.path.append(path)

from server import app as application
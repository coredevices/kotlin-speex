import libpebble2.services.voice as vs
from libpebble2.communication.transports.websocket import WebsocketTransport
from libpebble2.communication import PebbleConnection
import time
conn = PebbleConnection(WebsocketTransport("ws://192.168.0.133:9000"))
service = vs.VoiceService(conn)

recording_name = ""
fp = None

def session_handler(session_id, encoder_info):
    global recording_name, fp
    recording_name = f"recording_{time.strftime('%Y%m%d_%H%M%S')}.spx"
    fp = open(recording_name, "wb")
    print(f"Session {session_id} setup with encoder info: {encoder_info}. Recording to {recording_name}")

def audio_frame_handler(session_id, transfer):
    global fp
    for frame in transfer.frames:
        print(f"Received audio frame for session {session_id}, size: {len(frame.data)} bytes")
        fp.write(len(frame.data).to_bytes(1))
        fp.write(frame.data)

def stop_handler():
    global fp
    fp.close()
    print(f"Session stopped. Recording saved to {recording_name}")

service.register_handler("session_setup", session_handler)
service.register_handler("audio_frame", audio_frame_handler)
service.register_handler("audio_stop", stop_handler)

conn.connect()
conn.run_sync()
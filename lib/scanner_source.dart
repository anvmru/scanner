import 'dart:async';

import 'package:flutter/services.dart';
import 'scanner_listener_mixin.dart';

class ScannerSource {
  static const String channelName = 'com.seuic.scanner/plugin';
  static EventChannel? _scannerPlugin;
  static StreamSubscription? _subscription;

  static List<ScannerListenerMixin> listeners = [];

  /// You need to initialize it as necessary, when the program starts for the first time.
  static void init() {
    _scannerPlugin ??= const EventChannel(channelName);
    _subscription = _scannerPlugin!
        .receiveBroadcastStream()
        .listen(_onEvent, onError: _onError);
  }

  static void registerListener(ScannerListenerMixin listener) {
    if (!listeners.contains(listener)) listeners.add(listener);
  }

  static void unRegisterListener(ScannerListenerMixin listener) {
    if (listeners.contains(listener)) listeners.remove(listener);
  }

  /// You need to call this method to release resources when you exit the entire application.
  static void dispose() {
    listeners.clear();
    assert(_subscription != null);
    _subscription!.cancel();
  }

  static void _onEvent(data) {
    listeners.forEach((listener) => listener.checkRouteAndFireEvent(data));
  }

  static void _onError(Object error) {
    listeners.forEach((listener) => listener.onError(error));
  }
}

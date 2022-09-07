import 'package:flutter/material.dart';
import 'package:scanner/scanner_lifecycle_mixin.dart';
import 'package:scanner/scanner_listener_mixin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp>
    with ScannerLifecycleMixin<MyApp>, ScannerListenerMixin<MyApp> {
  var _code;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Code: $_code'),
            ],
          ),
        ),
      ),
    );
  }

  @override
  void onEvent(Object code) {
    setState(() {
      _code = code;
      print("ChannelPage: $code");
    });
  }

  @override
  void onError(Object error) {
    // TODO: implement onError
  }
}

import 'package:flutter/widgets.dart';
import 'scanner_source.dart';

/// Auto manage the lifecycle of Scanner plugin(ScannerSource).
/// Mixin to the state of the root widget.
mixin ScannerLifecycleMixin<T extends StatefulWidget> on State<T> {
  void initPdaLifecycle() {
    ScannerSource.init();
  }

  void disposePdaLifecycle() {
    ScannerSource.dispose();
  }

  @override
  void initState() {
    initPdaLifecycle();
    super.initState();
  }

  @override
  void dispose() {
    disposePdaLifecycle();
    super.dispose();
  }
}

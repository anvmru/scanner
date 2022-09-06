import 'package:flutter/widgets.dart';
import 'scanner_source.dart';

/// Auto manage the lifecycle of Scanner plugin(ScannerSource).
/// Mixin to the state of the root widget.
mixin ScannerLifecycleMixin<T extends StatefulWidget> on State<T> {
  void initScannerLifecycle() {
    ScannerSource.init();
  }

  void disposeScannerLifecycle() {
    ScannerSource.dispose();
  }

  @override
  void initState() {
    initScannerLifecycle();
    super.initState();
  }

  @override
  void dispose() {
    disposeScannerLifecycle();
    super.dispose();
  }
}

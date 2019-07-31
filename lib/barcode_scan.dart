import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

class BarcodeScanner {
  static const CameraAccessDenied = 'PERMISSION_NOT_GRANTED';
  static const UserCanceled = 'USER_CANCELED';
  static const MethodChannel _channel =
      const MethodChannel('com.apptreesoftware.barcode_scan');
  static Future<String> scan() async => await _channel.invokeMethod('scan');
}

class BarcodeScannerWidget extends StatelessWidget {
  final Function(String) onScanned;

  const BarcodeScannerWidget({
    Key key,
    @required this.onScanned
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "com.apptreesoftware.barcodescan.BarcodeScannerWidget",
        onPlatformViewCreated: _onViewCreated,
      );
    }
    return Text("Not implemented");
  }

  void _onViewCreated(int id) {
    final _eventChannel = EventChannel("com.apptreesoftware.barcodescan.BarcodeScannerWidgetController/$id");
    _eventChannel.receiveBroadcastStream().listen((res) {
      if (onScanned != null)
        onScanned(res as String);
    });
  }
}
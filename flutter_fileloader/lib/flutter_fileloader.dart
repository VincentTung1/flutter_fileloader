import 'dart:async';

import 'package:flutter/services.dart';

class FlutterFileloader {
  static const MethodChannel _channel =
      const MethodChannel('flutter_fileloader');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void loadFile(String path) async{
     await _channel.invokeMethod('loadFile',{
         'path' : path
     });
  }
}

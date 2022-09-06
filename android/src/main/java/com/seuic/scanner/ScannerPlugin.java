package com.seuic.scanner;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.seuic.scanner.ScannerFactory;
import com.seuic.scanner.Scanner;
import android.app.Activity;

/** ScannerPlugin */
public class ScannerPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Scanner scanner;

  public ScannerPlugin() {
    // Все классы плагинов для Android должны поддерживать no-args
    // конструктор. .По умолчанию конструктор no-args не объявлен,
    // но мы включаем его здесь для  ясности.
    // На этом этапе ваш плагин создаст instance, но он еще не привязан к Flutter.
    // Поэтому здесь нельзя обращаться или к его ресурсам или пробовать им управлять.
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    // Ваш плагин привязан к FlutterEngine.
    // Можно обращаться к движку через  binding.getFlutterEngine()
    // Или к BinaryMessenger с помощью
    // binding.getBinaryMessenger()
    // И контекст
    // binding.getApplicationContext()
    // А вот доступа к Activity здесь нет!

    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "scanner");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    // Ваш плагин отвязан от Flutter.
    // Необходимо очистить все ресурсы и ссылки, которые
    // созданы в onAttachedToFlutterEngine().

    channel.setMethodCallHandler(null);
  }
}

package com.apptreesoftware.barcodescan

import android.content.Context
import android.util.Log
import android.view.View
import com.google.zxing.Result
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarcodeScannerWidgetController(
    id: Int,
    messenger: BinaryMessenger,
    ctx: Context
): PlatformView, ZXingScannerView.ResultHandler, EventChannel.StreamHandler {

    private var scannerView: ZXingScannerView?
    private val eventChannel = EventChannel(messenger,"com.apptreesoftware.barcodescan.BarcodeScannerWidgetController/$id")
    private var eventSink: EventChannel.EventSink? = null
    private var lastScanned: String? = null

    init {
        val scanner = ZXingScannerView(ctx)
        scanner.setAutoFocus(true)
        scanner.setAspectTolerance(0.5f)
        scanner.setResultHandler(this)
        scannerView = scanner
        eventChannel.setStreamHandler(this)
    }

    override fun getView(): View? {
        scannerView?.startCamera()
        return scannerView
    }

    override fun dispose() {
        scannerView?.stopCamera()
        scannerView = null
    }

    override fun handleResult(rawResult: Result?) {
        val res = rawResult?.toString() ?: return
        lastScanned = res
        if (eventSink == null)
            Log.wtf("WTF", "QR SCANNED BUT EVENT SINK IS NULL")
        eventSink?.success(res)
    }

    override fun onListen(p0: Any?, eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
        if (lastScanned != null) {
            eventSink?.success(lastScanned)
            lastScanned = null
        }
    }

    override fun onCancel(p0: Any?) {}

}

class BarcodeScannerWidgetFactory(private val messenger: BinaryMessenger): PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    override fun create(ctx: Context, id: Int, o: Any?) =
        BarcodeScannerWidgetController(id, messenger, ctx)

}
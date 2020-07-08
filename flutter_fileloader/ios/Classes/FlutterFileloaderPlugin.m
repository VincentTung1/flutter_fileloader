#import "FlutterFileloaderPlugin.h"
#if __has_include(<flutter_fileloader/flutter_fileloader-Swift.h>)
#import <flutter_fileloader/flutter_fileloader-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_fileloader-Swift.h"
#endif

@implementation FlutterFileloaderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterFileloaderPlugin registerWithRegistrar:registrar];
}
@end

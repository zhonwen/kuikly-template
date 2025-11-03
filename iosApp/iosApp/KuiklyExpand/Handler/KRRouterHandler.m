#import "KRRouterHandler.h"
#import "KuiklyRenderViewController.h"
#import <UIKit/UIKit.h>

@implementation KRRouterHandler

+ (void)load {
    [KRRouterModule registerRouterHandler:[self new]];
    NSLog(@"[KRRouterHandler] 路由处理器已注册");
}

- (void)openPageWithName:(NSString *)pageName pageData:(NSDictionary *)pageData controller:(UIViewController *)controller {
    NSLog(@"[KRRouterHandler] 打开页面: %@, 数据: %@", pageName, pageData);
    NSLog(@"[KRRouterHandler] 当前控制器: %@", controller);
    NSLog(@"[KRRouterHandler] 当前导航控制器: %@", controller.navigationController);
    
    KuiklyRenderViewController *renderViewController = [[KuiklyRenderViewController alloc] initWithPageName:pageName pageData:pageData];
    [controller.navigationController pushViewController:renderViewController animated:YES];
    
    NSLog(@"[KRRouterHandler] 页面已推入导航栈");
    NSLog(@"[KRRouterHandler] 导航栈中的视图控制器数量: %lu", (unsigned long)controller.navigationController.viewControllers.count);
}

- (void)closePage:(UIViewController *)controller {
    NSLog(@"[KRRouterHandler] ===== 开始关闭页面 =====");
    NSLog(@"[KRRouterHandler] 关闭页面: %@", controller);
    NSLog(@"[KRRouterHandler] 导航控制器: %@", controller.navigationController);
    
    if (controller.navigationController) {
        NSLog(@"[KRRouterHandler] 导航栈中的视图控制器数量: %lu", (unsigned long)controller.navigationController.viewControllers.count);
        NSLog(@"[KRRouterHandler] 当前视图控制器在栈中的索引: %lu", (unsigned long)[controller.navigationController.viewControllers indexOfObject:controller]);
        NSLog(@"[KRRouterHandler] 导航栈中的所有视图控制器:");
        for (int i = 0; i < controller.navigationController.viewControllers.count; i++) {
            UIViewController *vc = controller.navigationController.viewControllers[i];
            NSLog(@"[KRRouterHandler]   [%d]: %@", i, vc);
        }
        
        if (controller.navigationController.viewControllers.count > 1) {
            NSLog(@"[KRRouterHandler] 执行popViewControllerAnimated:YES");
            [controller.navigationController popViewControllerAnimated:YES];
            NSLog(@"[KRRouterHandler] ✅ 页面已关闭");
        } else {
            NSLog(@"[KRRouterHandler] ❌ 无法关闭页面 - 导航栈中只有一个页面");
        }
    } else {
        NSLog(@"[KRRouterHandler] ❌ 导航控制器为空");
    }
    
    NSLog(@"[KRRouterHandler] ===== 关闭页面结束 =====");
}

@end
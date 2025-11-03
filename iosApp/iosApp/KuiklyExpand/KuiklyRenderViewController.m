#import "KuiklyRenderViewController.h"
#import "UINavigationController+FDFullscreenPopGesture.h"
#import <OpenKuiklyIOSRender/KuiklyRenderViewControllerBaseDelegator.h>
#import <OpenKuiklyIOSRender/KuiklyRenderContextProtocol.h>

#define HRWeakSelf __weak typeof(self) weakSelf = self;
@interface KuiklyRenderViewController()<KuiklyRenderViewControllerBaseDelegatorDelegate>

@property (nonatomic, strong) KuiklyRenderViewControllerBaseDelegator *delegator;

@end

@implementation KuiklyRenderViewController {
    NSDictionary *_pageData;
}

- (instancetype)initWithPageName:(NSString *)pageName pageData:(NSDictionary *)pageData {
    if (self = [super init]) {
        pageData = [self p_mergeExtParamsWithOriditalParam:pageData];
        _pageData = pageData;
        _delegator = [[KuiklyRenderViewControllerBaseDelegator alloc] initWithPageName:pageName pageData:pageData];
        _delegator.delegate = self;
        NSLog(@"[KuiklyRenderViewController] 初始化页面: %@", pageName);
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    NSLog(@"[KuiklyRenderViewController] viewDidLoad - 页面: %@", self.title ?: @"无标题11111");
    NSLog(@"[KuiklyRenderViewController] 导航控制器: %@", self.navigationController);
    NSLog(@"[KuiklyRenderViewController] 导航栈中的视图控制器数量: %lu", (unsigned long)self.navigationController.viewControllers.count);
    
    // 检查手势识别器
    if (self.navigationController) {
        NSLog(@"[KuiklyRenderViewController] 全屏手势识别器: %@", self.navigationController.fd_fullscreenPopGestureRecognizer);
        NSLog(@"[KuiklyRenderViewController] 手势识别器状态: %@", self.navigationController.fd_fullscreenPopGestureRecognizer.enabled ? @"启用" : @"禁用");
    }
    
    self.fd_prefersNavigationBarHidden = YES;
    self.view.backgroundColor = [UIColor whiteColor];
    [_delegator viewDidLoadWithView:self.view];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    [_delegator viewDidLayoutSubviews];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSLog(@"[KuiklyRenderViewController] viewWillAppear - 页面: %@", self.title ?: @"无标题");
    [_delegator viewWillAppear];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    NSLog(@"[KuiklyRenderViewController] viewDidAppear - 页面: %@", self.title ?: @"无标题");
    NSLog(@"[KuiklyRenderViewController] 当前导航栈: %@", self.navigationController.viewControllers);
    
    // 再次检查手势识别器状态
    if (self.navigationController) {
        NSLog(@"[KuiklyRenderViewController] 全屏手势识别器: %@", self.navigationController.fd_fullscreenPopGestureRecognizer);
        NSLog(@"[KuiklyRenderViewController] 手势识别器状态: %@", self.navigationController.fd_fullscreenPopGestureRecognizer.enabled ? @"启用" : @"禁用");
        NSLog(@"[KuiklyRenderViewController] 手势识别器代理: %@", self.navigationController.fd_fullscreenPopGestureRecognizer.delegate);
    }
    
    [_delegator viewDidAppear];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    NSLog(@"[KuiklyRenderViewController] viewWillDisappear - 页面: %@", self.title ?: @"无标题");
    [_delegator viewWillDisappear];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    NSLog(@"[KuiklyRenderViewController] viewDidDisappear - 页面: %@", self.title ?: @"无标题");
    [_delegator viewDidDisappear];
}

#pragma mark - private

- (NSDictionary *)p_mergeExtParamsWithOriditalParam:(NSDictionary *)pageParam {
    NSMutableDictionary *mParam = [(pageParam ?: @{}) mutableCopy];

    return mParam;
}

#pragma mark - KuiklyRenderViewControllerDelegatorDelegate

- (UIView *)createLoadingView {
    UIView *loadingView = [[UIView alloc] init];
    loadingView.backgroundColor = [UIColor whiteColor];
    return loadingView;
}

- (UIView *)createErrorView {
    UIView *errorView = [[UIView alloc] init];
    errorView.backgroundColor = [UIColor whiteColor];
    return errorView;
}

- (void)fetchContextCodeWithPageName:(NSString *)pageName resultCallback:(KuiklyContextCodeCallback)callback {
    if (callback) {
        // 返回对应framework名字
        callback(@"shared", nil);
    }
}

- (void)dealloc {
    NSLog(@"[KuiklyRenderViewController] dealloc - 页面: %@", self.title ?: @"无标题");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
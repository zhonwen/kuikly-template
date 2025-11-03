import promptAction from '@ohos.promptAction';
import router from '@ohos.router';

export class SafeBridgeModule {
  static readonly MODULE_NAME = "SafeBridgeModule";

  syncMode(): boolean {
    return false;
  }

  call(method: string, params: any, callback: any): any {
    try {
      console.log(`SafeBridgeModule call: ${method}`, params);
      
      if (!method) {
        console.error('SafeBridgeModule: method is null or undefined');
        return { error: 'Method is null or undefined' };
      }
      
      switch (method) {
        case 'toast':
          return this.toast(params);
        case 'log':
          return this.log(params);
        case 'closePage':
          return this.closePage();
        case 'openPage':
          return this.openPage(params);
        case 'showAlert':
          return this.showAlert(params, callback);
        default:
          console.warn(`SafeBridgeModule: Unknown method: ${method}`);
          return { error: `Unknown method: ${method}` };
      }
    } catch (error) {
      console.error(`SafeBridgeModule call error: ${error}`);
      return { error: error.message || 'Unknown error' };
    }
  }

  private toast(params: any): any {
    try {
      console.log('Toast called with params:', params);
      
      let content = 'Toast message';
      if (params && typeof params === 'object') {
        content = params['content'] || content;
      } else if (typeof params === 'string') {
        content = params;
      }
      
      console.log('Showing toast with content:', content);
      promptAction.showToast({
        message: content,
        duration: 2000
      });
      
      return { success: true, message: 'Toast shown' };
    } catch (error) {
      console.error('Toast error:', error);
      return { error: error.message };
    }
  }

  private log(params: any): any {
    try {
      if (params != null) {
        console.log(`SafeBridgeModule log: ${JSON.stringify(params)}`);
      }
      return { success: true, message: 'Log recorded' };
    } catch (error) {
      console.error('Log error:', error);
      return { error: error.message };
    }
  }

  private closePage(): any {
    try {
      router.back();
      return { success: true, message: 'Page closed' };
    } catch (error) {
      console.error('Close page error:', error);
      return { error: error.message };
    }
  }

  private openPage(params: any): any {
    try {
      if (params && typeof params === 'object') {
        const url = params['url'];
        if (url) {
          router.pushUrl({
            url: url,
            params: params['userData'] || {}
          });
          return { success: true, message: 'Page opened' };
        }
      }
      return { error: 'Invalid page parameters' };
    } catch (error) {
      console.error('Open page error:', error);
      return { error: error.message };
    }
  }

  private showAlert(params: any, callback: any): any {
    try {
      if (params && typeof params === 'object') {
        const title = params['title'] || '提示';
        const message = params['message'] || '';
        const buttons = params['buttons'] || ['确定'];
        
        promptAction.showDialog({
          title: title,
          message: message,
          buttons: buttons
        }).then((result) => {
          if (callback && typeof callback === 'function') {
            callback({ buttonIndex: result.index });
          }
        }).catch((error) => {
          console.error('Show alert error:', error);
          if (callback && typeof callback === 'function') {
            callback({ error: error.message });
          }
        });
        
        return { success: true, message: 'Alert shown' };
      }
      return { error: 'Invalid alert parameters' };
    } catch (error) {
      console.error('Show alert error:', error);
      return { error: error.message };
    }
  }
}


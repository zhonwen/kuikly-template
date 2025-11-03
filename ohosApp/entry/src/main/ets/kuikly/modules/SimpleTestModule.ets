import { KRAny, KuiklyRenderCallback } from '@kuikly-open/render';
import promptAction from '@ohos.promptAction';

export class SimpleTestModule {
  static readonly MODULE_NAME = "SimpleTestModule";

  syncMode(): boolean {
    return false;
  }

  call(method: string, params: KRAny, callback: KuiklyRenderCallback): KRAny {
    try {
      console.log(`SimpleTestModule call: ${method}`, params);
      
      switch (method) {
        case 'test':
          return this.testMethod(params);
        case 'toast':
          return this.toastMethod(params);
        case 'log':
          return this.logMethod(params);
        default:
          console.warn(`SimpleTestModule: Unknown method: ${method}`);
          return null;
      }
    } catch (error) {
      console.error(`SimpleTestModule call error: ${error}`);
      return { error: error.message || 'Unknown error' };
    }
  }

  private testMethod(params: KRAny): any {
    console.log('Test method called with:', params);
    return { success: true, message: 'Test method executed successfully' };
  }

  private toastMethod(params: KRAny): any {
    try {
      let content = 'Test toast';
      if (params && typeof params === 'object') {
        content = params['content'] || content;
      }
      
      console.log('Showing toast:', content);
      promptAction.showToast({
        message: content,
        duration: 2000
      });
      
      return { success: true, message: 'Toast shown' };
    } catch (error) {
      console.error('Toast method error:', error);
      return { error: error.message };
    }
  }

  private logMethod(params: KRAny): any {
    try {
      if (params) {
        console.log('Log method:', params);
      }
      return { success: true, message: 'Log recorded' };
    } catch (error) {
      console.error('Log method error:', error);
      return { error: error.message };
    }
  }
}


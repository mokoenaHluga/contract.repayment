import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Retrieve the token and requestId from sessionStorage
    const token = sessionStorage.getItem('userToken');
    const sessionId = sessionStorage.getItem('sessionId');

    if (request.url.includes('repayments/calculate')) {
      console.log('Adding token to headers');
      // Clone the request to add new headers
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          ...(sessionId ? { 'X-Request-Id': sessionId } : {})
        }
      });
    }

    return next.handle(request);
  }
}

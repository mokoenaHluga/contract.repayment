import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {BehaviorSubject, finalize, Observable, throwError} from "rxjs";
import {catchError, tap} from 'rxjs/operators';
import {LoginResponse} from "../model/login-response";

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {

  private apiUrl = 'http://localhost:8080/api';
  isLoading = new BehaviorSubject<boolean>(false);
  private isAuthenticated = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {}

  login(credentials: { userName: string, password: string }): Observable<any> {
    this.isLoading.next(true);
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login/v1`, credentials).pipe(
      finalize(() => this.isLoading.next(false)),
      tap(response => {
        sessionStorage.setItem('userToken', response.jwt);
        sessionStorage.setItem('sessionId', response.sessionId);
        if (response && response.jwt) {
          this.isAuthenticated.next(true);
        }
      }),
      catchError(error => {
        console.error('Login failed', error);
        return throwError(error);
      })
    );
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/register/v1`, user);
  }

  logout(): void {
    sessionStorage.removeItem('userToken');
    this.isAuthenticated.next(false);
  }

  isLoggedIn(): Observable<boolean> {
    return this.isAuthenticated.asObservable();
  }

  setAuthenticated(isAuthenticated: boolean): void {
    this.isAuthenticated.next(isAuthenticated);
  }
}

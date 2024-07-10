import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RepaymentOption } from '../model/repayment-option';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RepaymentService {
  private apiUrl = 'http://localhost:8080/api/repayments';

  constructor(private http: HttpClient, private router: Router) {}

  calculateOptions(amount: number): Observable<RepaymentOption[]> {
    const payload = { amount: amount };

    return this.http.post<RepaymentOption[]>(`${this.apiUrl}/calculate/v1`, payload).pipe(
      catchError(err => {
        console.error("Error during HTTP call:", err);
        if (err.status === 401) {
          this.handleTokenExpiry();
        }
        throw err;
      })
    );
  }

  private handleTokenExpiry() {
    sessionStorage.removeItem('userToken');
    this.router.navigate(['/login']).then(r => console.log("Token has expired, navigating to login page."));
    alert('Your session has expired. Please log in again.');
  }
}

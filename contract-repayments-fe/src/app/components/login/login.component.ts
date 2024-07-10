import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthServiceService} from "../../service/auth-service.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router) {
    this.loginForm = this.fb.group({
      userName: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.valid) {
      const credentials = this.loginForm.value;
      this.authService.login(credentials).subscribe(
        response => {
          sessionStorage.setItem('userToken', response.jwt)
          this.router.navigate(['/home']).then(r => console.log('Login successful'));},
        error => {
          console.error('Login error', error);
        }
      );
    }
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}

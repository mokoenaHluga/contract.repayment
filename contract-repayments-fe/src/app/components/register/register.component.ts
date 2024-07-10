import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthServiceService} from "../../service/auth-service.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;

  constructor(private fb: FormBuilder, private authService: AuthServiceService, private router: Router) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.registerForm.valid) {
      const newUser = this.registerForm.value;
      this.authService.register(newUser).subscribe(
        response => {
          console.log('Registration successful', response);
          this.router.navigate(['/login']);
        },
        error => {
          console.error('Registration error', error);
        }
      );
    }
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

}

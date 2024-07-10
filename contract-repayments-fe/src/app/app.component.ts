import {Component, OnInit} from '@angular/core';
import {AuthServiceService} from "./service/auth-service.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  isLoading: boolean = false;
  isLoggedIn: boolean = false;

  constructor(private authService: AuthServiceService) {
    this.authService.isLoading.subscribe((status: boolean) => {
      this.isLoading = status;
    });
  }

  ngOnInit() {
    this.authService.isLoggedIn().subscribe((loggedIn) => {
      this.isLoggedIn = loggedIn;
    });

    const token = localStorage.getItem('userToken');
    if (token) {
      this.authService.setAuthenticated(true);
    } else {
      this.authService.setAuthenticated(false);
    }
  }
}

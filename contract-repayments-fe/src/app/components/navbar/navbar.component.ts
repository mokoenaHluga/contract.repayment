import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { AuthServiceService } from "../../service/auth-service.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isLoggedIn: boolean = false;

  constructor(private authService: AuthServiceService, private router: Router) {}

  ngOnInit(): void {
    this.authService.isLoggedIn().subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']).then(r => console.log("Navigating to log in page"));
  }
}

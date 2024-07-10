import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepaymentService} from "../../service/repayment.service";
import {RepaymentOption} from "../../model/repayment-option";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  amountForm: FormGroup;
  repaymentOptions: RepaymentOption[] = [];

  constructor(private fb: FormBuilder, private repaymentService: RepaymentService) {
    this.amountForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.amountForm.valid) {
      this.repaymentService.calculateOptions(this.amountForm.value.amount).subscribe({
        next: (options) => this.repaymentOptions = options,
        error: (error) => console.error('Error fetching repayment options', error)
      });
    }
  }
}

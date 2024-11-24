package com.spring.cards.service.Impl;

import com.spring.cards.constants.LoansConstants;
import com.spring.cards.dto.LoansDto;
import com.spring.cards.entity.Loans;
import com.spring.cards.exception.LoanAlreadyExistsException;
import com.spring.cards.exception.ResourceNotFoundException;
import com.spring.cards.mapper.LoansMapper;
import com.spring.cards.repository.LoansRepository;
import com.spring.cards.service.ILoansService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class LoansService implements ILoansService {

    private LoansRepository loansRepository;

    @Override
    public void createLoan(String mobileNumber) {
        if(loansRepository.findByMobileNumber(mobileNumber).isPresent())
            throw new LoanAlreadyExistsException("Loan already exists for given mobile number "+ mobileNumber);
        Loans loans=createNewLoan(mobileNumber);
        loansRepository.save(loans);
    }

    private Loans createNewLoan(String mobileNumber){
        Loans newLoan = new Loans();
        long randomLoanNumber = 100000000000L + new Random().nextInt(900000000);
        newLoan.setLoanNumber(Long.toString(randomLoanNumber));
        newLoan.setMobileNumber(mobileNumber);
        newLoan.setLoanType(LoansConstants.HOME_LOAN);
        newLoan.setTotalLoan(LoansConstants.NEW_LOAN_LIMIT);
        newLoan.setAmountPaid(0);
        newLoan.setOutstandingAmount(LoansConstants.NEW_LOAN_LIMIT);
        return newLoan;
    }
    @Override
    public LoansDto fetchLoan(String mobileNumber){
        Loans loans=loansRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->new ResourceNotFoundException("Loans","mobile number",mobileNumber));
        return LoansMapper.mapToLoansDto(loans,new LoansDto());
    }

    @Override
    public boolean updateLoan(LoansDto loansDto){
        Loans loans=loansRepository.findByLoanNumber(loansDto.getLoanNumber()).orElseThrow(() ->new ResourceNotFoundException("Loans","mobile number",loansDto.getLoanNumber()));
        LoansMapper.mapToLoans(loansDto,loans);
        loansRepository.save(loans);
        return  true;
    }

    @Override
    public boolean deleteLoan(String mobileNumber){

        Loans loans=loansRepository.findByMobileNumber(mobileNumber).orElseThrow(() ->new ResourceNotFoundException("Loans","mobile number",mobileNumber));

        loansRepository.deleteById(loans.getLoanId());

        return true;
    }
}

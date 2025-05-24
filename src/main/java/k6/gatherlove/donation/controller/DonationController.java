package k6.gatherlove.donation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import k6.gatherlove.donation.dto.DonationRequest;
import k6.gatherlove.donation.model.Donation;
import k6.gatherlove.donation.service.DonationService;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public Donation create(@RequestBody DonationRequest req) {
        return donationService.createDonation(
                req.getUserId(),
                req.getAmount(),
                req.getCampaignId()
        );
    }

    @GetMapping
    public List<Donation> listAll() {
        return donationService.listAllDonations();
    }

    @GetMapping(params = "userId")
    public List<Donation> listByUser(@RequestParam String userId) {
        return donationService.listDonationsByUser(userId);
    }


    @GetMapping("/{id}")
    public Donation getById(@PathVariable String id) {
        Donation donation = donationService.findDonationById(id);
        if (donation == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Donation with id=" + id + " not found"
            );
        }
        return donation;
    }


    @DeleteMapping("/{id}")
    public void cancel(@PathVariable String id) {
        donationService.cancelDonation(id);
    }
}

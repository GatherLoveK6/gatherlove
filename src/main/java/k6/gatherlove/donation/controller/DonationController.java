package k6.gatherlove.donation.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                req.getAmount(),         // ← make sure this is one continuous call
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

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable String id) {
        donationService.cancelDonation(id);
    }
}

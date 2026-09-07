\c orcid

--
-- Statistics seed. statistic_values is populated at runtime by the scheduled
-- statistics-generation job (StatisticsManagerImpl.generateStatistics), which never
-- runs in dev, so StatisticsManagerImpl.getLatestLiveIds() throws NoResultException
-- (caught, returns 0) and the stats page/API shows nothing. One key + one row per
-- StatisticsEnum value gives every stat query a result. Values are representative
-- dev placeholders, not real figures.
--
INSERT INTO public.statistic_key (id, generation_date) VALUES (1, '2024-01-01 00:00:00+00');
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (1, 1, 'liveIds', 15000000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (2, 1, 'idsWithEducationQualification', 4200000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (3, 1, 'idsWithEmployment', 3800000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (4, 1, 'idsWithInvitedPositionDistinction', 210000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (5, 1, 'idsWithMembershipService', 640000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (6, 1, 'idsWithExternalId', 2900000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (7, 1, 'idsWithFunding', 720000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (8, 1, 'idsWithPeerReview', 830000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (9, 1, 'idsWithPersonId', 1100000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (10, 1, 'idsWithResearchResource', 90000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (11, 1, 'idsWithVerifiedEmail', 8100000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (12, 1, 'idsWithWorks', 5300000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (13, 1, 'works', 92000000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (14, 1, 'worksWithDois', 61000000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (15, 1, 'uniqueDois', 47000000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (16, 1, 'employment', 6200000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (17, 1, 'employmentUniqueOrg', 190000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (18, 1, 'educationQualification', 7100000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (19, 1, 'educationQualificationUniqueOrg', 160000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (20, 1, 'invitedPositionDistinction', 310000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (21, 1, 'invitedPositionDistinctionUniqueOrg', 45000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (22, 1, 'membershipService', 880000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (23, 1, 'membershipServiceUniqueOrg', 38000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (24, 1, 'funding', 1500000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (25, 1, 'fundingUniqueOrg', 52000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (26, 1, 'peerReview', 1900000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (27, 1, 'personId', 1300000);
INSERT INTO public.statistic_values (id, key_id, statistic_name, statistic_value) VALUES (28, 1, 'researchResource', 120000);
